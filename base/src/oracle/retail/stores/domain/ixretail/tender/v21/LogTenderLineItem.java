/* ===========================================================================
* Copyright (c) 1998, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/ixretail/tender/v21/LogTenderLineItem.java /main/22 2012/03/29 15:26:07 vtemker Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    vtemker   03/29/12 - Sensitive data from getDecryptedData() of
 *                         EncipheredData class fetched into byte array and
 *                         later, deleted
 *    cgreene   12/05/11 - updated from deprecated packages and used more
 *                         bigdecimal constants
 *    cgreene   09/12/11 - revert aba number encryption, which is not sensitive
 *    ohorne    08/18/11 - APF: check cleanup
 *    blarsen   08/02/11 - Renamed token to accountNumberToken to be
 *                         consistent.
 *    rrkohli   07/21/11 - encryption cr
 *    sgu       07/20/11 - merge with latest build
 *    sgu       07/19/11 - donot throw parse exception for datetime translation
 *    rrkohli   07/05/11 - POS Log related changes for Encryption CR
 *    cgreene   07/15/11 - removed encrypted expiration date from datamodel
 *    cgreene   07/07/11 - convert entryMethod to an enum
 *    cgreene   06/28/11 - rename hashed credit card field to token
 *    acadar    11/01/10 - use code instead of code name
 *    mchellap  10/15/10 - BUG#10202462 Log personal ID type as ID name instead
 *                         of code for store credit
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *    sswamygo  02/20/09 - Updated to use getCodeName() method to get the non
 *                         localized string
 *    mchellap  01/07/09 - Setting status for store credit
 *    cgreene   11/11/08 - switch to mail check getPrimaryAddress and Phone
 *                         methods
 *    abondala  11/06/08 - updated files related to reason codes
 *    abondala  11/05/08 - updated files related to the reason codes
 *                         CheckIDTypes and MailBankCheckIDTypes
 *    mkochumm  11/04/08 - i18n changes for phone and postalcode fields
 *
 * ===========================================================================
 * $Log:
 *    18   360Commerce 1.17        4/22/2008 1:41:26 AM   Manas Sahu      Gift
 *         Card authorization date time are needed to be proper values. The
 *         hardcoded values of 1980-01-01 was being used earlier. Code
 *         Reviewed by Naveen
 *    17   360Commerce 1.16        1/28/2008 4:31:09 PM   Sandy Gu
 *         Export foreign currency id, code and exchange rate for store credit
 *          and gift certificate foreign tender.
 *    16   360Commerce 1.15        12/14/2007 8:59:59 AM  Alan N. Sinton  CR
 *         29761: Removed non-PABP compliant methods and modified card RuleIfc
 *          to take an instance of EncipheredCardData.
 *    15   360Commerce 1.14        11/15/2007 10:28:24 AM Christian Greene
 *         Belize merge - add settlement data
 *    14   360Commerce 1.13        11/13/2007 5:18:35 PM  Alan N. Sinton  CR
 *         29598 - Added EncryptedExpirationDate to CreditDebit and fixed
 *         unittests.
 *    13   360Commerce 1.12        11/12/2007 4:28:28 PM  Alan N. Sinton  CR
 *         29598 - Changes for PABP.
 *    12   360Commerce 1.11        8/28/2007 10:53:27 AM  Maisa De Camargo CR
 *         28344 - Updated POSLog Exchange Rate Precision to 6 decimals
 *    11   360Commerce 1.10        8/22/2007 2:20:11 PM   Anda D. Cadar   CR
 *         28495: Send foreign Tender information to poslog and use a scale of
 *          5 when reading exchange rate in CO
 *    10   360Commerce 1.9         6/26/2007 11:52:03 AM  Ashok.Mondal
 *         Changes made to export the correct expiration date for issued store
 *          credit.
 *    9    360Commerce 1.8         6/26/2007 11:13:58 AM  Ashok.Mondal    I18N
 *         changes to export and import POSLog.
 *    8    360Commerce 1.7         4/25/2007 10:00:44 AM  Anda D. Cadar   I18N
 *         merge
 *    7    360Commerce 1.6         8/8/2006 4:38:35 PM    Charles D. Baker CR
 *         18,554 - Modified to check for empty string before attempting
 *         subString on phone number.
 *    6    360Commerce 1.5         1/25/2006 4:11:30 PM   Brett J. Larsen merge
 *          7.1.1 changes (aka. 7.0.3 fixes) into 360Commerce view
 *    5    360Commerce 1.4         1/22/2006 11:41:36 AM  Ron W. Haight
 *         Removed references to com.ibm.math.BigDecimal
 *    4    360Commerce 1.3         12/13/2005 4:43:48 PM  Barry A. Pape
 *         Base-lining of 7.1_LA
 *    3    360Commerce 1.2         3/31/2005 4:28:57 PM   Robert Pearse
 *    2    360Commerce 1.1         3/10/2005 10:23:17 AM  Robert Pearse
 *    1    360Commerce 1.0         2/11/2005 12:12:26 PM  Robert Pearse
 *: LogTenderLineItem.java,v $
 *    6    .v710     1.2.1.0.2.0 9/21/2005 13:39:53     Brendan W. Farrell
 *         Initial Check in merge 67.
 *    5    .v700     1.2.1.1     10/17/2005 15:27:18    Robert Zurga    For
 *         house account tender allow the card expiration date not to have have
 *         been specified.
 *    4    .v700     1.2.1.0     4/26/2005 13:28:19     Michael Wisbauer Added
 *         exporting of media issuer
 *    3    360Commerce1.2         3/31/2005 15:28:57     Robert Pearse
 *    2    360Commerce1.1         3/10/2005 10:23:17     Robert Pearse
 *    1    360Commerce1.0         2/11/2005 12:12:26     Robert Pearse
 *
 *   Revision 1.5.2.5  2005/01/27 20:01:11  kmcbride
 *   @scr 7948: Removed hard-coded "Credit" string in charge type generation for POSLog, and replaced it with code to determine if the charge was a debit or credit
 *
 *   Revision 1.5.2.4  2005/01/21 22:41:14  jdeleau
 *   @scr 7888 merge Branch poslogconf into v700
 *
 *   Revision 1.5.2.2.2.1  2005/01/20 16:37:23  jdeleau
 *   @scr 7888 Various POSLog fixes from mwright
 *
 *   Revision 1.5.2.3  2005/01/14 00:09:29  bwf
 *   @scr 7913 Check to make sure there is not a null character as the only Track3 data.
 *
 *   Revision 1.5.2.2  2004/12/08 02:51:53  rdunsmore
 *   @scr CO2230 - fixing nulls getting into poslog xml in tender track data
 *
 *   Revision 1.5.2.1  2004/10/29 03:57:30  mwright
 *   Merge from top of tree (Mods involving change line item)
 *
 *   Revision 1.6  2004/10/29 03:28:44  mwright
 *   Add change as tender line item
 *
 *   Revision 1.5  2004/08/16 00:09:08  mwright
 *   Added code to export money order tender
 *   Extract credit card expiry date based on entry method
 *
 *   Revision 1.4  2004/08/10 07:17:11  mwright
 *   Merge (3) with top of tree
 *
 *   Revision 1.3.2.4  2004/08/09 23:23:09  mwright
 *   Changed logging of expiry date to correct format
 *   Added customerVerification element for ID type and related fields in instant credit enrollment first retail transaction
 *
 *   Revision 1.3.2.3  2004/08/09 12:41:53  mwright
 *   Ensure expiry date is in CCYY-MM format
 *   Make sure track data are present before attempting to log them
 *   Export the send check reason code (as obtained from the ID Type)
 *   Correct export of first/middle/last names
 *
 *   Revision 1.3.2.2  2004/08/06 02:30:30  mwright
 *   Treat E-Check line normal check
 *   Added phone number and area code for check tender
 *   Added first/last name and ID type for store credit
 *
 *   Revision 1.3.2.1  2004/08/01 22:50:57  mwright
 *   Set MICR number, phone and area code, conversion flag for check tender
 *   Set first name, last name and ID type for voucher tender
 *   Set organization ID for purchase order tender
 *   Set face value amount in gift certificate tender
 *
 *   Revision 1.3  2004/06/24 09:15:10  mwright
 *   POSLog v2.1 (second) merge with top of tree
 *
 *   Revision 1.2.2.3  2004/06/23 00:27:42  mwright
 *   Added the tender line item using the schema type, instead of appendChild()
 *   Set change tender type to CASH - not good, but better than nothing.
 *
 *   Revision 1.2.2.2  2004/06/15 06:34:35  mwright
 *   Set coupon number in Scan Code rather than primary label
 *
 *   Revision 1.2.2.1  2004/06/10 10:54:23  mwright
 *   Updated to use schema types in commerce services
 *
 *   Revision 1.2  2004/05/06 03:20:27  mwright
 *   Initial revision for POSLog v2.1 merge with top of tree
 *
 *   Revision 1.1.2.3  2004/04/28 11:28:59  mwright
 *   Added type code for store credit voucher
 *
 *   Revision 1.1.2.2  2004/04/26 22:06:49  mwright
 *   Changed from RetailTransactionAddress to 360 own implmentation of RetailTransactionAddress360, because ixretail element is not extensible
 *   SToring state in description element, because element is not extensible and description is unused
 *
 *   Revision 1.1.2.1  2004/04/13 07:32:34  mwright
 *   Initial revision for v2.1
 *
 * ===========================================================================
 */
package oracle.retail.stores.domain.ixretail.tender.v21;

import java.awt.Point;
import java.math.BigDecimal;

import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.commerceservices.ixretail.schematypes.v21.RetailTransactionAddress360Ifc;
import oracle.retail.stores.commerceservices.ixretail.schematypes.v21.RetailTransactionLineItemIfc;
import oracle.retail.stores.commerceservices.ixretail.schematypes.v21.RetailTransactionTenderChangeIfc;
import oracle.retail.stores.commerceservices.ixretail.schematypes.v21.RetailTransactionTenderIfc;
import oracle.retail.stores.commerceservices.ixretail.schematypes.v21.TenderAuthorizationIfc;
import oracle.retail.stores.commerceservices.ixretail.schematypes.v21.TenderCreditDebitIfc;
import oracle.retail.stores.commerceservices.ixretail.schematypes.v21.TenderCustomerVerificationIfc;
import oracle.retail.stores.commerceservices.ixretail.schematypes.v21.TenderForeignCurrencyIfc;
import oracle.retail.stores.commerceservices.ixretail.schematypes.v21.TenderSendCheckIfc;
import oracle.retail.stores.commerceservices.ixretail.schematypes.v21.TenderVoucherIfc;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.ixretail.IXRetailConstantsV21Ifc;
import oracle.retail.stores.domain.ixretail.IXRetailGateway;
import oracle.retail.stores.domain.ixretail.lineitem.v21.LogLineItem;
import oracle.retail.stores.domain.ixretail.tender.LogTenderLineItemIfc;
import oracle.retail.stores.domain.ixretail.utility.LogAddressIfc;
import oracle.retail.stores.domain.ixretail.utility.LogGiftCardIfc;
import oracle.retail.stores.domain.tender.AuthorizableTenderIfc;
import oracle.retail.stores.domain.tender.TenderAlternateCurrencyIfc;
import oracle.retail.stores.domain.tender.TenderChargeIfc;
import oracle.retail.stores.domain.tender.TenderCheckIfc;
import oracle.retail.stores.domain.tender.TenderCouponIfc;
import oracle.retail.stores.domain.tender.TenderDebitIfc;
import oracle.retail.stores.domain.tender.TenderGiftCardIfc;
import oracle.retail.stores.domain.tender.TenderGiftCertificateIfc;
import oracle.retail.stores.domain.tender.TenderLineItemConstantsIfc;
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

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * This class creates the elements for a TenderLineItem
 *
 * @version $Revision: /main/22 $
 */
public class LogTenderLineItem extends LogLineItem implements LogTenderLineItemIfc
{
    /**
     * revision number supplied by source-code-control system
     */
    public static final String revisionNumber = "$Revision: /main/22 $";

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
    public Element createElement(TenderLineItemIfc tenderLineItem,
                                 Document doc,
                                 Element el,
                                 int sequenceNumber)
    throws XMLConversionException
    {

        RetailTransactionLineItemIfc lineItemElement = (RetailTransactionLineItemIfc)el;

        super.createElement(doc, el, null, false, sequenceNumber);

        //lineItemElement.setSequenceNumber(Integer.toString(sequenceNumber));
        // void flag assumed false

        RetailTransactionTenderIfc tenderLineItemElement = getSchemaTypesFactory().getRetailTransactionTenderInstance();
        lineItemElement.setTender(tenderLineItemElement);


        // create entry method element, where applicable
        lineItemElement.setEntryMethod(getEntryMethod(tenderLineItem));

        // tender ID is tender type descriptor
        TenderTypeMapIfc map = DomainGateway.getFactory().getTenderTypeMapInstance();
        tenderLineItemElement.setTenderID(map.getIXRetailDescriptor(tenderLineItem.getTypeCode()));

        // tender sub code is not supported...do we need to do anything about it???


        tenderLineItemElement.setAmount(getSchemaTypesFactory().getPOSLogAmountInstance().initialize(currency(tenderLineItem.getAmountTender())));
        tenderLineItemElement.setCurrencyID(tenderLineItem.getCurrencyID()); //I18N

        // handle foreign currency item, if needed
        if (tenderLineItem instanceof TenderAlternateCurrencyIfc && ((TenderAlternateCurrencyIfc) tenderLineItem).getAlternateCurrencyTendered() != null)
        {
            TenderForeignCurrencyIfc foreignAmount = getSchemaTypesFactory().getTenderForeignCurrencyInstance();
            createForeignCurrencyElements(tenderLineItem, foreignAmount);
            tenderLineItemElement.setForeignCurrency(foreignAmount);
        }

        if (tenderLineItem instanceof AuthorizableTenderIfc)
        {
            createAuthorizationElements(tenderLineItem, tenderLineItemElement);
        }

        // create tender-specific elements
        createTenderSpecificElements(tenderLineItem, tenderLineItemElement);

        lineItemElement.setTender(tenderLineItemElement);

        return lineItemElement;

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
    public Element createTenderChangeElement(CurrencyIfc changeAmount,
                                             Document doc,
                                             Element el,
                                             int sequenceNumber)
    throws XMLConversionException
    {
        super.createElement(doc, el, null, false, sequenceNumber);

        RetailTransactionLineItemIfc lineItemElement = (RetailTransactionLineItemIfc)el;

        RetailTransactionTenderIfc tenderLineItemElement = getSchemaTypesFactory().getRetailTransactionTenderInstance();
        lineItemElement.setTender(tenderLineItemElement);

        // There are no longer change line items, only tenders.
        // So we add the change tender type and amount into the tender as well:
        tenderLineItemElement.setTenderID("Cash");        // we assume change is always cash, becasue we don't know what it is

        // we only ever add one change element:
        RetailTransactionTenderChangeIfc changeElement = getSchemaTypesFactory().getRetailTransactionTenderChangeInstance();
        tenderLineItemElement.setTenderChange(new RetailTransactionTenderChangeIfc[] {changeElement} );

        // The amount in the tender line item is mandatory, so we set it to zero (not any more)
        //tenderLineItemElement.setAmount(getSchemaTypesFactory().getPOSLogAmountInstance().initialize(currency(null)));
        tenderLineItemElement.setAmount(getSchemaTypesFactory().getPOSLogAmountInstance().initialize(currency(changeAmount)));
        tenderLineItemElement.setCurrencyID(changeAmount.getType().getCurrencyId()); //I18N

        // only mandatory element is amount
        changeElement.setAmount(getSchemaTypesFactory().getPOSLogAmountInstance().initialize(currency(changeAmount.abs())));
        changeElement.setTenderType("Cash");        // this is the lesser of the evils

        return lineItemElement;

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
    protected String getEntryMethod(TenderLineItemIfc tenderLineItem)
    {
        EntryMethod entryMethod = EntryMethod.Manual;
        String ixRetailEntryMethod = IXRetailConstantsV21Ifc.ELEMENT_VALUE_KEYED;
        switch(tenderLineItem.getTypeCode())
        {
            case TenderLineItemIfc.TENDER_TYPE_CHARGE:
              entryMethod = ((TenderChargeIfc) tenderLineItem).getEntryMethod();
              // if magswipe, set msr
              if (entryMethod!= null &&
                  entryMethod.equals(EntryMethod.Swipe))
              {
                ixRetailEntryMethod = IXRetailConstantsV21Ifc.ELEMENT_VALUE_MSR;
              }
              break;
            case TenderLineItemIfc.TENDER_TYPE_DEBIT:
              entryMethod = ((TenderDebitIfc) tenderLineItem).getEntryMethod();
              // if magswipe, set msr
              if (entryMethod != null &&
                  entryMethod.equals(EntryMethod.Swipe))
              {
                ixRetailEntryMethod = IXRetailConstantsV21Ifc.ELEMENT_VALUE_MSR;
              }
              break;
            case TenderLineItemIfc.TENDER_TYPE_E_CHECK:
            case TenderLineItemIfc.TENDER_TYPE_CHECK:
              entryMethod = ((TenderCheckIfc) tenderLineItem).getEntryMethod();
              // if auto/micr, set micr
              if (EntryMethod.Automatic.equals(entryMethod) || EntryMethod.Micr.equals(entryMethod))
              {
                ixRetailEntryMethod = IXRetailConstantsV21Ifc.ELEMENT_VALUE_MICR;
              }
              break;
            case TenderLineItemIfc.TENDER_TYPE_COUPON:
              entryMethod = ((TenderCouponIfc) tenderLineItem).getEntryMethod();
              // if auto, set scanned
              if (entryMethod != null &&
                  entryMethod.equals(EntryMethod.Automatic))
              {
                ixRetailEntryMethod = IXRetailConstantsV21Ifc.ELEMENT_VALUE_SCANNED;
              }
              break;
            case TenderLineItemIfc.TENDER_TYPE_GIFT_CARD:
              entryMethod = ((TenderGiftCardIfc) tenderLineItem).getEntryMethod();
              // if magswipe, set msr
              if (entryMethod != null &&
                  entryMethod.equals(EntryMethod.Swipe))
              {
                ixRetailEntryMethod = IXRetailConstantsV21Ifc.ELEMENT_VALUE_MSR;
              }
              break;
            case TenderLineItemIfc.TENDER_TYPE_GIFT_CERTIFICATE:
              entryMethod = ((TenderGiftCertificateIfc) tenderLineItem).getEntryMethod();
              // if auto, set scanned
              if (entryMethod != null &&
                  entryMethod.equals(EntryMethod.Automatic))
              {
                ixRetailEntryMethod = IXRetailConstantsV21Ifc.ELEMENT_VALUE_SCANNED;
              }
              break;
            case TenderLineItemIfc.TENDER_TYPE_STORE_CREDIT:
              entryMethod = ((TenderStoreCreditIfc) tenderLineItem).getEntryMethod();
              // if auto, set scanned
              if (entryMethod != null &&
                  entryMethod.equals(EntryMethod.Automatic))
              {
                ixRetailEntryMethod = IXRetailConstantsV21Ifc.ELEMENT_VALUE_SCANNED;
              }
              break;
            case TenderLineItemIfc.TENDER_TYPE_TRAVELERS_CHECK:
            case TenderLineItemIfc.TENDER_TYPE_CASH:
            case TenderLineItemIfc.TENDER_TYPE_MONEY_ORDER:
            default:
              break;
        }
        return ixRetailEntryMethod;
    }

    /**
     * Creates element for RetailTransactionForeignCurrency type.
     *
     * @param tenderLineItem tender line item
     * @param el tender line item element
     * @exception XMLConversionException thrown if error occurs
     */
    protected void createForeignCurrencyElements(TenderLineItemIfc tenderLineItem, TenderForeignCurrencyIfc amount)
    throws XMLConversionException
    {
        TenderAlternateCurrencyIfc alternateCurrencyTender = (TenderAlternateCurrencyIfc) tenderLineItem;

        // get code and amount
        amount.setCurrencyCode(alternateCurrencyTender.getAlternateCurrencyTendered().getCountryCode());
        amount.setOriginalFaceAmount(currency(alternateCurrencyTender.getAlternateCurrencyTendered()));
        amount.setExchangeRate(alternateCurrencyTender.getAlternateCurrencyTendered().getBaseConversionRate());
        amount.setDateTime(dateValue(new EYSDate(1980, 1, 1, 0, 0, 0)));       // this is required by schema, no info available for it
    }

    /**
     * Creates Authorization elements.
     *
     * @param tenderLineItem tender line item
     * @param el tender line item element
     * @exception XMLConversionException thrown if error occurs
     */
    protected void createAuthorizationElements(TenderLineItemIfc tenderLineItem, RetailTransactionTenderIfc el)
    throws XMLConversionException
    {
        AuthorizableTenderIfc authorizableTender = (AuthorizableTenderIfc) tenderLineItem;

        TenderAuthorizationIfc auth = getSchemaTypesFactory().getTenderAuthorizationInstance();

        // set amount
        auth.setRequestedAmount(getSchemaTypesFactory().getPOSLogAmountInstance().initialize(currency(authorizableTender.getAmountTender())));

        // set authorization code - it is mandatory, so make sure it is not null:
        String authCode = authorizableTender.getAuthorizationCode();
        if (authCode == null)
        {
            authCode = "";
        }
        auth.setAuthorizationCode(authCode);

        // reference number (not supported) required in v2.1
        auth.setReferenceNumber(IXRetailConstantsV21Ifc.ELEMENT_VALUE_NOT_SUPPORTED);

        // provider ID (not supported) optional in v2.1, but required by use case:
        auth.setProviderID(IXRetailConstantsV21Ifc.ELEMENT_VALUE_NOT_SUPPORTED);

        // Merchant number is required by use case, not used by import
        auth.setMerchantNumber(IXRetailConstantsV21Ifc.ELEMENT_VALUE_NOT_SUPPORTED);

        // This is required for some use cases (2.03.03) (It is not used by the import system)
        auth.setAuthorizedChangeAmount(BigDecimal.ZERO);
        auth.setAuthorizingTermID(IXRetailConstantsV21Ifc.ELEMENT_VALUE_NOT_SUPPORTED);

        if (authorizableTender.getAuthorizedDateTime() == null)
        {
            auth.setAuthorizationDateTime(dateValue(new EYSDate()));
        }
        else
        {
            auth.setAuthorizationDateTime(authorizableTender.getAuthorizedDateTime().dateValue());
        }
        auth.setAuthorizationSettlementData(authorizableTender.getSettlementData());

        // host authorized (not supported) optional in v2.1
        // force online (not supported) optional in v2.1

        // check for electronic signature
        boolean flag = false;
        // at this time, we only support signature for credit
        if (authorizableTender instanceof TenderChargeIfc)
        {
            // check for existence of signature data
            TenderChargeIfc tenderCharge = (TenderChargeIfc) authorizableTender;
            // if data exists and has a length, set flag to true
            if (tenderCharge.getSignatureData() != null &&
                ((Point[]) tenderCharge.getSignatureData()).length > 0)
            {
                flag = true;
            }
        }
        auth.setElectronicSignature(new Boolean(flag));

        // authorization method code. NOTE: we use the DESCRIPTION element for the method code....
        auth.setAuthorizationDescription(authorizableTender.getAuthorizationMethod());

        // get signature, if it exists
        createSignatureElement(authorizableTender, auth);

        el.setAuthorization(new TenderAuthorizationIfc[] {auth} );

    }

    /**
     * Creates tender-specific elements.
     *
     * @param tenderLineItem tender line item
     * @param el tender line item element
     * @exception XMLConversionException thrown if error occurs
     */
    protected void createTenderSpecificElements(TenderLineItemIfc tenderLineItem, RetailTransactionTenderIfc el)
    throws XMLConversionException
    {
        switch(tenderLineItem.getTypeCode())
        {
            case TenderLineItemIfc.TENDER_TYPE_CHARGE:
            case TenderLineItemIfc.TENDER_TYPE_DEBIT:
              createTenderChargeElements(tenderLineItem, el);
              break;

            case TenderLineItemIfc.TENDER_TYPE_E_CHECK:
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

            case TenderLineItemIfc.TENDER_TYPE_CASH:
              // take no action
              break;

            case TenderLineItemIfc.TENDER_TYPE_MONEY_ORDER:
                // no extra info required
                break;

            default:
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
    protected void createTenderCheckElements(TenderLineItemIfc tenderLineItem, RetailTransactionTenderIfc el)
    throws XMLConversionException
    {
        oracle.retail.stores.commerceservices.ixretail.schematypes.v21.TenderCheckIfc checkElement = getSchemaTypesFactory().getTenderCheckInstance();

        TenderCheckIfc tenderCheck = (TenderCheckIfc) tenderLineItem;

        // use ABA number for bank ID
        checkElement.setBankID(tenderCheck.getABANumber());

        // set check number
        checkElement.setCheckNumber(tenderCheck.getCheckNumber());

        // set account number
        checkElement.setEncryptedAccountNumber(tenderCheck.getAccountNumberEncipheredData().getEncryptedNumber());
        checkElement.setMaskedAccountNumber(tenderCheck.getAccountNumberEncipheredData().getMaskedNumber());

        byte[] personalID = new byte[0];
        try
        {
            personalID = tenderCheck.getPersonalID().getDecryptedNumber();
            // take identification information only if exists
            if (personalID.length != 0)
            {
                TenderCustomerVerificationIfc idElement = getSchemaTypesFactory()
                        .getTenderCustomerVerificationInstance();

                // get ID type, issuer, number and DOB
                idElement.setIDType(tenderCheck.getPersonalIDType().getCode());
                idElement.setIssuer(tenderCheck.getIDIssuer());
                idElement.setEncryptedIDNumber(tenderCheck.getPersonalID().getEncryptedNumber());
                idElement.setMaskedIDNumber(tenderCheck.getPersonalID().getMaskedNumber());

                if (tenderCheck.getDateOfBirth() != null)
                {
                    idElement.setBirthdate(dateValue(tenderCheck.getDateOfBirth()));
                }
                el.setCustomerVerification(idElement);
            }
        }
        finally
        {
            Util.flushByteArray(personalID);
        }

        checkElement.setEncryptedMicrNumber(tenderCheck.getMICREncipheredData().getEncryptedNumber());
        checkElement.setMaskedMicrNumber(tenderCheck.getMICREncipheredData().getMaskedNumber());
        String phone = tenderCheck.getPhoneNumber();       // assumed to contain area code???? must we extract it?? Jdbc writer simply concatenates area and number
        if (!Util.isEmpty(phone))
        {
        	checkElement.setPhoneNumber(phone);
        }
        checkElement.setConversionFlag(tenderCheck.getConversionFlag());

        el.setCheck(checkElement);

    }

    /**
     * Creates tender charge (debit or credit) elements.
     *
     * @param tenderLineItem tender line item
     * @param el tender line item element
     * @exception XMLConversionException thrown if error occurs
     */
    protected void createTenderChargeElements(TenderLineItemIfc tenderLineItem, RetailTransactionTenderIfc el)
    throws XMLConversionException
    {
        TenderCreditDebitIfc chargeElement = getSchemaTypesFactory().getTenderCreditDebitInstance();
        el.setCreditDebit(chargeElement);

        TenderChargeIfc tenderCharge = (TenderChargeIfc) tenderLineItem;

        chargeElement.setPrimaryAccountNumber(tenderCharge.getCardNumber());            // this line used to be in createAccountIDElement() in v1.0

        int cardType = tenderCharge.getTypeCode();
        if(cardType == TenderLineItemConstantsIfc.TENDER_TYPE_DEBIT)
        {
            chargeElement.setCardType(IXRetailConstantsV21Ifc.ENUMERATION_CARD_TYPE_DEBIT);
        }
        else if(cardType == TenderLineItemConstantsIfc.TENDER_TYPE_CHARGE)
        {
            chargeElement.setCardType(IXRetailConstantsV21Ifc.ENUMERATION_CARD_TYPE_CREDIT);
        }
        else
        {
            chargeElement.setCardType("Unknown");
        }

        chargeElement.setTenderMediaIssuerID(tenderCharge.getCardType());
        chargeElement.setTenderTypeCodeString(tenderCharge.getTypeCodeString());

        // get track data
        if ((tenderCharge.getTrack2Data() != null)&&(tenderCharge.getTrack2Data().length > 0))
        {
            String track2 = new String(tenderCharge.getTrack2Data());
            chargeElement.setTrack2Data(track2);
        }

        chargeElement.setAccountNumberToken(tenderCharge.getAccountNumberToken());
        chargeElement.setMaskedAccountNumber(tenderCharge.getMaskedCardNumber());
        TenderCustomerVerificationIfc idElement = getSchemaTypesFactory().getTenderCustomerVerificationInstance();

        idElement.setIDType(tenderCharge.getPersonalIDType().getCode());
        idElement.setIssuer(tenderCharge.getIDCountry());
        idElement.setProvince(tenderCharge.getIDState());
        idElement.setExpirationDate(dateValue(tenderCharge.getIDExpirationDate()));

        // the schema demands an ID number as well:
        idElement.setEncryptedIDNumber("");
        idElement.setMaskedIDNumber("");

        el.setCustomerVerification(idElement);

        // usage of issue sequence is unknown - optional in v2.1
        // usage of service code is unknown - optional in v2.1
    }

    /**
     * Creates element of specified name for track data if data exists.
     *
     * @param elementName element name
     * @param trackData byte array of track data
     * @param el parent element
     * @exception XMLConversionException is thrown if error occurs
     */
    protected void createTrackDataElement(String elementName,
                                          byte[] trackData,
                                          Element el)
    throws XMLConversionException
    {                                   // begin createTrackDataElement()
        if (trackData != null &&
            trackData.length > 0)
        {
            createTextNodeElement
              (elementName,
               new String(trackData),
               el);
        }
    }

    /**
     * Creates tender send check elements.
     *
     * @param tenderLineItem tender line item
     * @param el tender line item element
     * @exception XMLConversionException thrown if error occurs
     */
    protected void createTenderSendCheckElements(TenderLineItemIfc tenderLineItem,
                                                 RetailTransactionTenderIfc el)
    throws XMLConversionException
    {
        TenderSendCheckIfc sendCheckElement = getSchemaTypesFactory().getTenderSendCheckInstance();
        PersonNameIfc payeeName = ((TenderMailBankCheckIfc) tenderLineItem).getPayeeName();

        TenderMailBankCheckIfc tenderMailLineItem = (TenderMailBankCheckIfc)tenderLineItem;
        sendCheckElement.setReasonCode(tenderMailLineItem.getPersonalIDType().getCode());
        sendCheckElement.setPayeeFirstName(payeeName.getFirstName());
        sendCheckElement.setPayeeMiddleName(payeeName.getMiddleName());
        sendCheckElement.setPayeeLastName(payeeName.getLastName());

        el.setSendCheck(sendCheckElement);

        // add address
        RetailTransactionAddress360Ifc addressElement = getSchemaTypesFactory().getRetailTransactionAddressInstance();
        LogAddressIfc logAddress = IXRetailGateway.getFactory().getLogAddressInstance();
        logAddress.createElement(((TenderMailBankCheckIfc)tenderLineItem).getPrimaryAddress(),
                               parentDocument,
                               addressElement);
        el.setAddress(addressElement);

    }

    /**
     * Creates tender store credit elements.
     *
     * @param tenderLineItem tender line item
     * @param el tender line item element
     * @exception XMLConversionException thrown if error occurs
     */
    protected void createTenderStoreCreditElements(TenderLineItemIfc tenderLineItem,
                                                   RetailTransactionTenderIfc el)
    throws XMLConversionException
    {
        TenderVoucherIfc storeCreditElement = getSchemaTypesFactory().getTenderVoucherInstance();

        TenderStoreCreditIfc storeCredit = (TenderStoreCreditIfc) tenderLineItem;

        // create action attribute
        // the type code must be from the enumeration...it can't be extended!
        // so we store the state in the description element...since it is not used for anything else
        storeCreditElement.setDescription(storeCredit.getState());     // v1.0 had this as "Action", we use description to contain "state"

        // set type code for store credit:
        storeCreditElement.setTypeCode("Voucher");

        storeCreditElement.setSerialNumber(storeCredit.getStoreCreditID());     // Use serial number to contain store credit ID, whatever that is

        if (storeCredit.getState().equals("ISSUED") && storeCredit.getExpirationDate() != null)
        {
            storeCreditElement.setExpirationDate(dateValue(storeCredit.getExpirationDate()));
        }
        else    // the expiration date is mandatory in the XML schema, so we put in a dummy date:
        {
            storeCreditElement.setExpirationDate(dateValue(new EYSDate(1980, 1, 1, 0, 0, 0)));
        }

        storeCreditElement.setState(storeCredit.getState());

        // Use case demands a face value - use the voucher amount
        storeCreditElement.setFaceValueAmount(getSchemaTypesFactory().getPOSLogAmountInstance().initialize(currency(storeCredit.getAmount())));
        storeCreditElement.setCurrencyID(storeCredit.getCurrencyID()); //I18N

        TenderCustomerVerificationIfc idElement = getSchemaTypesFactory().getTenderCustomerVerificationInstance();

        idElement.setFirstName(storeCredit.getFirstName());
        idElement.setLastName(storeCredit.getLastName());
        idElement.setIDType(storeCredit.getPersonalIDType().getCode());


        // the schema demands an ID number as well:
        idElement.setEncryptedIDNumber("");
        idElement.setMaskedIDNumber("");

        el.setCustomerVerification(idElement);

        el.setVoucher(storeCreditElement);
    }

    /**
     * Creates tender travelers check elements.
     *
     * @param tenderLineItem tender line item
     * @param el tender line item element
     * @exception XMLConversionException thrown if error occurs
     */
    protected void createTenderTravelersCheckElements(TenderLineItemIfc tenderLineItem,
                                                      RetailTransactionTenderIfc el)
    throws XMLConversionException
    {
        oracle.retail.stores.commerceservices.ixretail.schematypes.v21.TenderCheckIfc checkElement = getSchemaTypesFactory().getTenderCheckInstance();
        checkElement.setCheckCount(Short.toString(((TenderTravelersCheckIfc) tenderLineItem).getNumberChecks()));
        el.setTravelersCheck(checkElement);
    }

    /**
     * Creates tender purchase order elements.
     *
     * @param tenderLineItem tender line item
     * @param el tender line item element
     * @exception XMLConversionException thrown if error occurs
     */
    private void createTenderPurchaseOrderElements(TenderLineItemIfc tenderLineItem,
                                                   RetailTransactionTenderIfc el)
    throws XMLConversionException
    {
        oracle.retail.stores.commerceservices.ixretail.schematypes.v21.TenderPurchaseOrderIfc poElement = getSchemaTypesFactory().getTenderPurchaseOrderInstance();
        TenderPurchaseOrderIfc po = (TenderPurchaseOrderIfc) tenderLineItem;
        poElement.setPurchaseOrderID(po.getPurchaseOrderNumber());

        poElement.setOrganizationID(po.getAgencyName());

        // these elements are required, but never read by the import:
        poElement.setEffectiveDate(dateValue(new EYSDate(1980, 1, 1, 0, 0, 0)));
        poElement.setPartyID("N/A");
        poElement.setAuthorizedAmount(getSchemaTypesFactory().getPOSLogAmountInstance().initialize(currency(po.getAmountTender())));

        el.setPurchaseOrder(poElement);
    }

    /**
     * Creates tender gift certificate elements.
     *
     * @param tenderLineItem tender line item
     * @param el tender line item element
     * @exception XMLConversionException thrown if error occurs
     */
    protected void createTenderGiftCertificateElements(TenderLineItemIfc tenderLineItem,
                                                       RetailTransactionTenderIfc el)
    throws XMLConversionException
    {
        oracle.retail.stores.commerceservices.ixretail.schematypes.v21.TenderVoucherIfc certElement = getSchemaTypesFactory().getTenderVoucherInstance();

        TenderGiftCertificateIfc cert = (TenderGiftCertificateIfc) tenderLineItem;

        certElement.setTypeCode("GiftCertificate");
        certElement.setSerialNumber(cert.getGiftCertificateNumber());
        certElement.setIssuingStoreNumberID(cert.getStoreNumber());
        // this is a mandatory field...we set it to 1/1/1980 so we know it is not really there
        certElement.setExpirationDate(dateValue(new EYSDate(1980, 1, 1, 0, 0, 0)));

        // v1.0 sent the amount tendered in the face value field.....
        //certElement.setFaceValueAmount(getSchemaTypesFactory().getPOSLogAmountInstance().initialize(currency(cert.getAmountTender())));
          certElement.setFaceValueAmount(getSchemaTypesFactory().getPOSLogAmountInstance().initialize(currency(cert.getFaceValueAmount())));
          certElement.setCurrencyID(cert.getCurrencyID()); //I18N

        el.setVoucher(certElement);
    }

    /**
     * Creates tender gift card elements.
     *
     * @param tenderLineItem tender line item
     * @param el tender line item element
     * @exception XMLConversionException thrown if error occurs
     */
    protected void createTenderGiftCardElements(TenderLineItemIfc tenderLineItem,
                                                RetailTransactionTenderIfc el)
    throws XMLConversionException
    {
        oracle.retail.stores.commerceservices.ixretail.schematypes.v21.TenderGiftCardIfc cardElement = getSchemaTypesFactory().getTenderGiftCardInstance();
        TenderGiftCardIfc giftCardTender = (TenderGiftCardIfc) tenderLineItem;

        LogGiftCardIfc logGiftCard = IXRetailGateway.getFactory().getLogGiftCardInstance();

        logGiftCard.createElement(giftCardTender.getGiftCard(), null, null, cardElement);

        EYSDate authorizationDateTime = ((AuthorizableTenderIfc)tenderLineItem).getAuthorizedDateTime();
        if (authorizationDateTime != null)
        {
            cardElement.getAuthorization().setAuthorizationDateTime(authorizationDateTime.dateValue());
        }
        el.setGiftCard(cardElement);
    }

    /**
     * Creates tender manufacturer coupon elements.
     *
     * @param tenderLineItem tender line item
     * @param el tender line item element
     * @exception XMLConversionException thrown if error occurs
     */
    protected void createTenderCouponElements(TenderLineItemIfc tenderLineItem,
                                              RetailTransactionTenderIfc el)
    throws XMLConversionException
    {
        oracle.retail.stores.commerceservices.ixretail.schematypes.v21.TenderCouponIfc couponElement = getSchemaTypesFactory().getTenderCouponInstance();

        TenderCouponIfc tenderCoupon = (TenderCouponIfc) tenderLineItem;

        // add coupon type attribute
        String ixRetailCouponType = IXRetailConstantsV21Ifc.ATTRIBUTE_VALUE_MANUFACTURER;
        switch(tenderCoupon.getCouponType())
        {
            case TenderCouponIfc.COUPON_TYPE_STORE:
              ixRetailCouponType = IXRetailConstantsV21Ifc.ATTRIBUTE_VALUE_STORE;
              break;
            case TenderCouponIfc.COUPON_TYPE_ELECTRONIC:
              ixRetailCouponType = IXRetailConstantsV21Ifc.ATTRIBUTE_VALUE_ELECTRONIC;
              break;
            case TenderCouponIfc.COUPON_TYPE_MANUFACTURER:
            default:
              break;
        }
        couponElement.setCouponType(ixRetailCouponType);

        couponElement.setScanCode(((TenderCouponIfc) tenderLineItem).getCouponNumber());

        // expiry date is mandatory in the schema, so we set a dummy date here:
        couponElement.setExpirationDate(dateValue(new EYSDate(1980, 1, 1, 0, 0, 0)));

        // primary label is mandatory, so we set a dummy:
        couponElement.setPrimaryLabel("Unknown");

        // Use case demands these:
        couponElement.setQuantity(getSchemaTypesFactory().getPOSLogQuantityInstance().initialize(BigDecimal.ZERO));
        couponElement.setManufacturerID("Unknown");
        couponElement.setFamilyCode("Unknown");

        el.setCoupon(couponElement);

    }

    /**
     * Creates elements for electronic signature, if it exists.
     *
     * @param authorizableTender authorizable tender object
     * @param el element to which signature image is to be appended
     * @exception throws XMLConversionException if error occurs
     */
    protected void createSignatureElement(AuthorizableTenderIfc authorizableTender, TenderAuthorizationIfc el)
    throws XMLConversionException
    {
        // at this time, signature captured only on charge
        if (authorizableTender instanceof TenderChargeIfc)
        {
            TenderChargeIfc tenderCharge = (TenderChargeIfc) authorizableTender;
            if (tenderCharge.getSignatureData() != null)
            {
                Point[] data = (Point[]) tenderCharge.getSignatureData();
                StringBuffer value = new StringBuffer();
                if (data != null)
                {
                    Point   p = null;

                    for (int i = 0; i < data.length; i++)
                    {
                        p = data[i];
                        value.append("x" + Integer.toString(p.x) +
                                     "y" + Integer.toString(p.y));
                    }
                }
                else
                {
                    value.append("null");
                }

                if (value.length() > 0)
                {
                    el.setElectronicSignatureImage(value.toString());
                }
            }
        }
    }

}
