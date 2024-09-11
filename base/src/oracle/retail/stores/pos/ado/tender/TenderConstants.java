/* ===========================================================================
* Copyright (c) 1998, 2013, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ado/tender/TenderConstants.java /main/24 2013/09/04 09:10:26 icole Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    icole  08/30/13 - Add support for check approval sequence number
 *    icole  03/06/13 - Print Trace Number on Debit receipt if exists, else print
 *                      System Audit Trace Number if exists per ACI's
 *                      requirements.
 *    blarse 08/27/12 - Merge from project Echo (MPOS) into trunk.
 *    jswan  03/26/12 - Modified to support centralized gift certificate and
 *                      store credit.
 *    cgreen 04/03/12 - removed deprecated methods
 *    jswan  09/12/11 - Modifications for reversals of Gift Cards when escaping
 *                      from the Tender Tour.
 *    cgreen 09/12/11 - added support for setting balance left on prepaid card
 *    cgreen 09/12/11 - revert aba number encryption, which is not sensitive
 *    cgreen 09/09/11 - deprecated MSR_MODEL
 *    ohorne 08/18/11 - added Personal ID constants for entry method and track
 *                      data
 *    cgreen 08/08/11 - Switch giftcard action to requestSubtype to avoid clash
 *                      with requestType.
 *    cgreen 07/20/11 - added support for requiring signature for icc cards
 *    rrkohl 07/19/11 - encryption cr
 *    blarse 07/14/11 - Added tender attrib keys for fields required for
 *                      PinComm reversals.
 *    cgreen 07/12/11 - update generics
 *    cgreen 06/29/11 - add token column and remove encrypted/hashed account
 *                      number column in credit-debit tender table.
 *    blarse 06/16/11 - renamed TOKEN to PAYMENT_SERVICE_TOKEN
 *    cgreen 06/02/11 - Tweaks to support Servebase chipnpin
 *    blarse 05/12/11 - Added TOKEN for card tenders
 *    ohorne 05/12/11 - added FLOOR_LIMIT_AMOUNT
 *    sgu    02/03/11 - check in all
 *    asinto 09/22/10 - Adding Credit Card Accountability Responsibility and
 *                      Disclosure Act of 2009 changes.
 *    asinto 05/28/10 - KSN bytes need to be captured from CPOI device and
 *                      formatted in the ISD request message for debit
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *    mkochumm  01/30/09 - add country index
 *    sgu       12/23/08 - fixed the crash in foreign check tender
 *    cgreene   11/06/08 - add isCollected to tenders for printing just
 *                         collected tenders
 *    abondala  11/06/08 - updated files related to reason codes
 *
 * ===========================================================================
 * $Log:
 *    11   360Commerce 1.10        6/12/2008 4:32:39 PM   Charles D. Baker CR
 *         32040 - Updated to avoid clearing tax exempt status unless a) we're
 *          removing a tax exempt tender and b) there are not remaining
 *         tenders that are tax exempt purchase orders. Code review by Jack
 *         Swan.
 *    10   360Commerce 1.9         6/12/2008 4:12:17 PM   Maisa De Camargo CR
 *         32031 - Applied a non-invasive fix for Post Void of Transactions
 *         tendered with GC (ISD).
 *         The ISD messageType for GC is based on the existence of a GC. (FA
 *         for a new card, F for existent card). Code Reviewed by Jack Swan.
 *    9    360Commerce 1.8         12/12/2007 6:47:38 PM  Alan N. Sinton  CR
 *         29761: FR 8: Prevent repeated decryption of PAN data.
 *    8    360Commerce 1.7         11/21/2007 1:59:17 AM  Deepti Sharma   CR
 *         29598: changes for credit/debit PAPB
 *    7    360Commerce 1.6         11/15/2007 10:10:33 AM Christian Greene
 *         Belize merge - add settlement and auth constants
 *    6    360Commerce 1.5         6/28/2007 5:17:17 PM   Charles D. Baker CR
 *         27124 - Updated to account for alignments provided by
 *         CurrencyIfc.toGroupFormattedString()
 *    5    360Commerce 1.4         4/5/2006 5:59:46 AM    Akhilashwar K. Gupta
 *         CR-3861: As per BA decision, reverted back the changes done earlier
 *          to fix the CR i.e. addition of following 4 fields in Store Credit
 *         and related code:
 *         - RetailStoreID
 *         - WorkstationID
 *         - TransactionSequenceNumber
 *         - BusinessDayDate
 *    4    360Commerce 1.3         3/15/2006 11:43:06 PM  Akhilashwar K. Gupta
 *         CR-3861: Added new Constant BUSINESS_DAY_DATE
 *    3    360Commerce 1.2         3/31/2005 4:30:23 PM   Robert Pearse
 *    2    360Commerce 1.1         3/10/2005 10:25:55 AM  Robert Pearse
 *    1    360Commerce 1.0         2/11/2005 12:14:49 PM  Robert Pearse
 *   $
 *
 *   Revision 1.21  2004/08/31 19:12:35  blj
 *   @scr 6855 - cleanup gift card credit code and fix defects found by PBY
 *
 *   Revision 1.20  2004/07/28 01:05:28  blj
 *   @scr 6495 updated status so that they all match.
 *
 *   Revision 1.19  2004/07/16 01:11:54  jdeleau
 *   @scr 5446 Correct the way phone numbers are sent to e-journal for
 *   mail bank checks, remove the use of deprecated constants.
 *
 *   Revision 1.18  2004/07/15 23:22:45  crain
 *   @scr 5280 Gift Certificates issued in Training Mode can be Tendered outside of Training Mode
 *
 *   Revision 1.17  2004/05/26 23:09:03  crain
 *   @scr 5062 Purchase Order- Taxable status missing from journal when agency is other/business
 *
 *   Revision 1.16  2004/05/25 15:11:41  blj
 *   @scr 5115 - resolution for printing issues
 *
 *   Revision 1.15  2004/05/24 21:45:39  crain
 *   @scr 5105 Tender Redeem_Gift Cert Redeem w/ Disc. Receipt Incorrect
 *
 *   Revision 1.14  2004/05/11 16:08:47  blj
 *   @scr 4476 - more rework for store credit tender.
 *
 *   Revision 1.13  2004/05/02 01:54:05  crain
 *   @scr 4553 Redeem Gift Certificate
 *
 *   Revision 1.12  2004/04/29 15:07:19  crain
 *   @scr 4553 Redeem Gift Certificate
 *
 *   Revision 1.11  2004/04/22 19:12:03  crain
 *   @scr 4206 Updating Javadoc
 *
 *   Revision 1.10  2004/04/14 22:37:53  epd
 *   @scr 4322 Tender Invariant work.  Specifically for change invariant
 *
 *   Revision 1.9  2004/04/12 18:41:53  blj
 *   @scr - 3872 added Redeem label for state.
 *
 *   Revision 1.8  2004/04/09 22:46:13  bjosserand
 *   @scr 0 remove commented code
 *
 *   Revision 1.7  2004/04/09 22:39:54  bjosserand
 *   @scr 4093 Transaction Reentry
 *
 *   Revision 1.6  2004/03/22 15:51:03  crain
 *   @scr 4105 Foreign Currency
 *
 *   Revision 1.5  2004/02/27 16:39:40  bjosserand
 *   @scr 0 Mail Bank Check
 *
 *   Revision 1.4  2004/02/19 17:39:41  rsachdeva
 *   @scr 3820 ISD Integration
 *
 *   Revision 1.3  2004/02/12 16:47:55  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:19:47  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:11  cschellenger
 *   updating to pvcs 360store-current
 *
 *    Rev 1.20   Feb 08 2004 12:12:32   bjosserand
 * Mail Bank Check.
 *
 *    Rev 1.19   Feb 05 2004 13:46:26   rhafernik
 * log4j changes
 *
 *    Rev 1.18   Feb 04 2004 16:27:44   bjosserand
 * Mail Bank Check.
 *
 *    Rev 1.17   Jan 29 2004 13:12:00   bjosserand
 * Mail Bank Check.
 *
 *    Rev 1.16   Dec 29 2003 12:04:02   crain
 * Added PO constant
 * Resolution for 3421: Tender redesign
 *
 *    Rev 1.15   Dec 18 2003 21:10:46   crain
 * Changed constant
 * Resolution for 3421: Tender redesign
 *
 *    Rev 1.14   Dec 17 2003 11:30:20   crain
 * Added PO constants
 * Resolution for 3421: Tender redesign
 *
 *    Rev 1.13   Dec 10 2003 13:34:44   crain
 * Added conversion rate
 * Resolution for 3421: Tender redesign
 *
 *    Rev 1.12   Dec 09 2003 15:57:40   rsachdeva
 * Alternate Currency
 * Resolution for POS SCR-3551: Tender using Canadian Cash
 *
 *    Rev 1.11   Dec 08 2003 11:31:50   blj
 * added firstname and lastname to the constants.
 *
 *    Rev 1.10   Dec 08 2003 10:53:42   rsachdeva
 * Alternate Currency
 * Resolution for POS SCR-3551: Tender using Canadian Cash
 *
 *    Rev 1.9   Dec 07 2003 18:49:26   crain
 * Added foreign amount
 * Resolution for 3421: Tender redesign
 *
 *    Rev 1.8   Nov 25 2003 12:11:26   bwf
 * Added response_type.
 * Resolution for 3429: Check/ECheck Tender
 *
 *    Rev 1.7   Nov 20 2003 15:34:16   crain
 * Added gift certificate constants
 * Resolution for 3421: Tender redesign
 *
 *    Rev 1.6   Nov 18 2003 14:51:04   crain
 * Added gift certificate constants
 * Resolution for 3421: Tender redesign
 *
 *    Rev 1.5   Nov 16 2003 13:15:40   crain
 * Added gift certificate constants
 * Resolution for 3421: Tender redesign
 *
 *    Rev 1.4   Nov 13 2003 13:25:44   bwf
 * Added some check constants.
 * Resolution for 3429: Check/ECheck Tender
 *
 *    Rev 1.3   Nov 10 2003 15:21:56   blj
 * added constants for Gift Card
 *
 *    Rev 1.2   Nov 07 2003 12:59:34   bwf
 * Added PHONE_NUMBER.
 * Resolution for 3429: Check/ECheck Tender
 *
 *    Rev 1.1   Nov 06 2003 13:57:36   epd
 * updates for OCC approval
 *
 *    Rev 1.0   Nov 04 2003 11:13:12   epd
 * Initial revision.
 *
 *    Rev 1.5   Nov 03 2003 11:45:04   epd
 * added id_country field
 *
 *    Rev 1.4   Nov 01 2003 15:08:46   epd
 * dev updates
 *
 *    Rev 1.3   Oct 31 2003 16:45:44   epd
 * Added constants for ID information
 *
 *    Rev 1.2   Oct 28 2003 15:13:44   epd
 * added constant for entry method
 *
 *    Rev 1.1   Oct 27 2003 19:43:52   crain
 * Added constants for coupon
 * Resolution for 3421: Tender redesign
 *
 *    Rev 1.0   Oct 17 2003 12:33:44   epd
 * Initial revision.
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.ado.tender;

import oracle.retail.stores.domain.utility.PhoneConstantsIfc;
/**
 * This class defines the constants to be used as keys
 * in the hash used as an argument to addTender().
 */
public class TenderConstants
{
    /**
     * This class need not ever be instantiated.
     */
    private TenderConstants()
    {
    }

    ////////////////////////////////////////////
    //  The constants...
    ////////////////////////////////////////////

    /**  The type of the tender */
    public static final String TENDER_TYPE = "TENDER_TYPE";
    /**  The amount of the tender */
    public static final String AMOUNT = "AMOUNT";
    /**  The tender was given or received */
    public static final String COLLECTED = "COLLECTED";
    /**  Foreign tender currency */
    public static final String FOREIGN_CURRENCY = "FOREIGN_CURRENCY";
    /** The amount of Alternate Tender */
    public static final String ALTERNATE_AMOUNT = "ALTERNATE_AMOUNT";
    /** The alternate tender currency type */
    public static final String ALTERNATE_CURRENCY_TYPE = "ALTERNATE_CURRENCY_TYPE";
    /**  The number representing the tender (i.e. Credit Card number) */
    public static final String NUMBER = "NUMBER";
    /**  The expiration date of the tender */
    public static final String EXPIRATION_DATE = "EXPIRATION_DATE";
    /**  The field used to determine whether or not to evaluate tender limits */
    public static final String CHECK_LIMITS = "CHECK_LIMITS";
    /**
     * The MSR model from the UI if a tender is swiped
     * @deprecated as of 13.4 (I think) we don't carry around card swipes for tenders any longer.
     */
    public static final String MSR_MODEL = "MSR_MODEL";
    /** The EncipheredCardData instance */
    public static final String ENCIPHERED_CARD_DATA = "ENCIPHERED_CARD_DATA";
    /** The Integrated Chip Card details */
    public static final String ICC_DETAILS = "ICC_DETAILS";
    /** The amount of money left on a prepaid credit card. */
    public static final String PREPAID_REMAINING_BALANCE = "PREPAID_REMAINING_BALANCE";
    /** The key to specify whether the customer's signature is required. */
    public static final String SIGNATURE_REQUIRED = "SIGNATURE_REQUIRED";
    /**  The PIN number */
    public static final String PIN_NUMBER = "PIN_NUMBER";
    /**  The Additional Security Info value */
    public static final String ADDITIONAL_SECURITY_INFO = "ADDITIONAL_SECURITY_INFO";
    /**  The ID number (Driver's License, etc) */
    public static final String ID_NUMBER = "ID_NUMBER";
    /**  The Entry Method of the ID */
    public static final String ID_ENTRY_METHOD = "ID_ENTRY_METHOD";
    /**  The Track 1 Data if an ID is swiped */
    public static final String ID_TRACK_1_DATA = "ID_TRACK_1_DATA";
    /**  The Track 2 Data if an ID is swiped */
    public static final String ID_TRACK_2_DATA = "ID_TRACK_2_DATA";
    /**  The ID type */
    public static final String ID_TYPE = "ID_TYPE";
    /**  The ID type */
    public static final String LOCALIZED_ID_TYPE = "LOCALIZED_ID_TYPE";
    /**  The ID country index */
    public static final String ID_COUNTRY_INDEX = "ID_COUNTRY_INDEX";
    /**  The ID state */
    public static final String ID_COUNTRY = "ID_COUNTRY";
    /**  The ID state */
    public static final String ID_STATE = "ID_STATE";
    /**  The ID expiration date */
    public static final String ID_EXPIRATION_DATE = "ID_EXPIRATION_DATE";
    /**  Date of birth */
    public static final String DATE_OF_BIRTH = "DATE_OF_BIRTH";
    /**  Country */
    public static final String COUNTRY = "COUNTRY";
    /**  ABA number */
    public static final String ABA_NUMBER = "ABA_NUMBER";
    /**  Account number */
    public static final String ACCOUNT_NUMBER = "ACCOUNT_NUMBER";
    /**  MICR data */
    public static final String MICR_DATA = "MICR_DATA";
    /**  Check number */
    public static final String CHECK_NUMBER = "CHECK_NUMBER";
    /** Authorization amount */
    public static final String AUTH_AMOUNT = "AUTH_AMOUNT";
    /** Authorization code */
    public static final String AUTH_CODE = "AUTH_CODE";
    /** Authorization Method */
    public static final String AUTH_METHOD = "AUTH_METHOD";
    /** Authorization Required Flag */
    public static final String AUTH_REQUIRED = "AUTH_REQUIRED";
    /** Authorization Response */
    public static final String AUTH_RESPONSE = "AUTH_RESPONSE";
    /** Authorization Response */
    public static final String AUTH_RESPONSE_CODE = "AUTH_RESPONSE_CODE";
    /** Authorization Status */
    public static final String AUTH_STATUS = "AUTH_STATUS";
    /** Financial network status */
    public static final String FINANCIAL_NETWORK_STATUS = "FINANCIAL_NETWORK_STATUS";
    /** Settlement data */
    public static final String SETTLEMENT_DATA = "SETTLEMENT_DATA";
    /** Authorization date */
    public static final String AUTH_DATE_TIME = "AUTH_DATA_TIME";
    /** Count (generic count for any tender that counts something) */
    public static final String COUNT = "COUNT";
    /** Coupon type */
    public static final String COUPON_TYPE = "COUPON_TYPE";
    /** Coupon number */
    public static final String COUPON_NUMBER = "COUPON_NUMBER";
    /** Entry Method */
    public static final String ENTRY_METHOD = "ENTRY_METHOD";
    /** OCC approval obtained (only used for credit) */
    public static final String OCC_APPROVAL_CODE = "OCC_APPROVAL_CODE";
    /** phone number */
    public static final String PHONE_NUMBER = "PHONE_NUMBER";
    /**
     * A key of this type is expect to have represented by
     * a collection of PhoneIfc objects.
     */
    public static final String PHONES = "PHONES";
    /** remaining balance */
    public static final String REMAINING_BALANCE = "REMAINING_BALANCE";
    /** original balance */
    public static final String ORIGINAL_BALANCE = "ORIGINAL_BALANCE";
    /** network status */
    public static final String NETWORK_STATUS = "NETWORK_STATUS";
    /** sequence number */
    public static final String SEQUENCE_NUMBER = "SEQUENCE_NUMBER";
    /** check auth type */
    public static final String CHECK_AUTH_TYPE = "CHECK_AUTH_TYPE";
    /** check authorization sequence number */
    public static final String CHECK_AUTH_SEQUENCE_NUMBER = "AUTH_SEQUENCE_NUMBER";
    /** store number */
    public static final String STORE_NUMBER = "STORE_NUMBER";
    /** redeem transaction id */
    public static final String REDEEM_TRANSACTION_ID = "REDEEM_TRANSACTION_ID";
    /** issue date */
    public static final String ISSUE_DATE = "ISSUE_DATE";
    /** redeem date */
    public static final String REDEEM_DATE = "REDEEM_DATE";
    /** redeem state */
    public static final String ISSUE = "ISSUED";
    /**  certificate type */
    public static final String CERTIFICATE_TYPE = "CERTIFICATE_TYPE";
    /** response type */
    public static final String RESPONSE_TYPE = "RESPONSE_TYPE";
    /** first name */
    public static final String FIRST_NAME = "FIRST_NAME";
    /** last name */
    public static final String LAST_NAME = "LAST_NAME";
    /** conversion code */
    public static final String CONVERSION_CODE = "CONVERSION_CODE";
    /** conversion rate */
    public static final String CONVERSION_RATE = "CONVERSION_RATE";
    /** agency name */
    public static final String AGENCY_NAME = "AGENCY_NAME";
    /** other agency name */
    public static final String OTHER_AGENCY_NAME = "OTHER_AGENCY_NAME";
    /** transaction non taxable */
    public static final String TRANSACTION_NON_TAXABLE = "TRANSACTION_NON_TAXABLE";
    /** face value amount */
    public static final String FACE_VALUE_AMOUNT = "FACE_VALUE_AMOUNT";
    /** Certificate document */
    public static final String CERTIFICATE_DOCUMENT = "CERTIFICATE_DOCUMENT";
    /** address line 1 */
    public static final String ADDRESS_1 = "ADDRESS_1";
    /**    address line 2 */
    public static final String ADDRESS_2 = "ADDRESS_2";
    /**    address line 3 */
    public static final String ADDRESS_3 = "ADDRESS_3";
    /** city */
    public static final String CITY = "CITY";
    /** state */
    public static final String STATE = "STATE";
    /** postal code 1 */
    public static final String POSTAL_CODE_1 = "POSTAL_CODE_1";
    /**    postal code 2 */
    public static final String POSTAL_CODE_2 = "POSTAL_CODE_2";
    /** business customer (boolean) */
    public static final String BUSINESS_CUSTOMER = "BUSINESS_CUSTOMER";
    /** full name */
    public static final String NAME = "NAME";
    /** business name */
    public static final String BUSINESS_NAME = "BUSINESS_NAME";
    /** transaction number */
    public static final String TRANSACTION_NUMBER = "TRANSACTION_NUMBER";
    /** card number token from the payment service */
    public static final String ACCOUNT_NUMBER_TOKEN = "ACCOUNT_NUMBER_TOKEN";
    
    /** phone number type (work, home, etc.) */
    public static final Integer PHONE_TYPE = new Integer(PhoneConstantsIfc.PHONE_TYPE_HOME);
    /** depleted change amount (see Gift Card) */
    public static final String CASH_CHANGE_AMOUNT = "CASH_CHANGE_AMOUNT";
    /** gift card credit flag */
    public static final String GIFT_CARD_CREDIT_FLAG = "GIFT_CARD_CREDIT_FLAG";
    /** gift card existing */
    public static final String GIFT_CARD_EXISTING = "GIFT_CARD_EXISTING";
    /** true */
    public static final String TRUE = "TRUE";
    /** false */
    public static final String FALSE = "FALSE";
    /** request type */
    public static final String REQUEST_TYPE = "REQUEST_TYPE";
    /** request sub-type */
    public static final String REQUEST_SUBTYPE = "REQUEST_SUBTYPE";
    /** request code */
    public static final String REQUEST_CODE = "REQUEST_CODE";
    /** action code */
    public static final String ACTION_CODE = "ACTION_CODE";
    /** transaction type */
    public static final String TRANSACTION_TYPE = "TRANSACTION_TYPE";
    /** void tender */
    public static final String VOID_TENDER = "VOID_TENDER";

    /**
     *  PIN COMM REVERSAL FIELDS
     */
    public static final String JOURNAL_KEY = "JOURNAL_KEY";
    public static final String AUTH_SEQUENCE_NUMBER = "AUTH_SEQUENCE_NUMBER";
    public static final String LOCAL_TIME = "LOCAL_TIME";
    public static final String LOCAL_DATE = "LOCAL_DATE";
    public static final String ACCOUNT_DATA_SOURCE = "ACCOUNT_DATA_SOURCE";
    public static final String PAYMENT_SERVICE_INDICATOR = "PAYMENT_SERVICE_INDICATOR";
    public static final String TRANSACTION_ID = "TRANSACTION_ID";
    public static final String VALIDATION_CODE = "VALIDATION_CODE";
    public static final String AUTH_SOURCE = "AUTH_SOURCE";
    public static final String HOST_REFERENCE = "HOST_REFERENCE";
    public static final String TRACE_NUMBER = "TRACE_NUMBER";
    public static final String SYSTEM_TRACE_AUDIT_NUMBER = "SYSTEM_AUDIT_TRACE_NUMBER";

    /** discount amount */
    public static final String DISCOUNT_AMOUNT = "DISCOUNT_AMOUNT";
    
    /** Offline Floor Limit Amount */
    public static final String FLOOR_LIMIT_AMOUNT = "FLOOR_LIMIT_AMOUNT";
    /**
     * TAXABLE_STATUS
     */
    public static final String TAXABLE_STATUS = "TAXABLE_STATUS";
    /** Taxable tax status */
    public static final String TAXABLE = "Taxable";
    /** Tax exempt tax status */
    public static final String TAX_EXEMPT = "Tax Exempt";
    /** training mode (boolean) */
    public static final String TRAINING_MODE = "TRAINING_MODE";

    // Added for Credit Card Accountability Responsibility and Disclosure Act of 2009
    /** Constant value for AccountARP */
    public static String ACCOUNT_APR = "ACCOUNT_APR";

    /** Constant value for AccountARP Type */
    public static String ACCOUNT_APR_TYPE = "ACCOUNT_APR_TYPE";

    /** Constant value for PromotionARP */
    public static String PROMOTION_APR = "PROMOTION_APR";

    /** Constant value for PromotionARP Type */
    public static String PROMOTION_APR_TYPE = "PROMOTION_APR_TYPE";

    /** Constant value for PromotionDescription */
    public static String PROMOTION_DESCRIPTION = "PROMOTION_DESCRIPTION";

    /** Constant value for PromotionDuration */
    public static String PROMOTION_DURATION = "PROMOTION_DURATION";
    
    /** Enciphered data for Personal ID number */
    public static final String ENCIPHERED_DATA_ID_NUMBER = "ENCIPHERED_DATA_ID_NUMBER";

    /** Enciphered data related to MICR */
    public static final String ENCIPHERED_DATA_MICR_NUMBER = "ENCIPHERED_DATA_MICR_NUMBER";

    /** Enciphered data for Account Number */
    public static final String ENCIPHERED_DATA_ACCOUNT_NUMBER = "ENCIPHERED_DATA_ACCOUNT_NUMBER";

    /** Gift Card Account Type; needed for reversal. */
    public static final String GIFT_CARD_ACCOUNT_TYPE = "GIFT_CARD_ACCOUNT_TYPE";

    /** Reference Code; needed for gift card reversal. */
    public static final String REFERENCE_CODE = "REFERENCE_CODE";
    
}
