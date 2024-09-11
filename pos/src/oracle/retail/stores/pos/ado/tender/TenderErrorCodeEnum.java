/* ===========================================================================
* Copyright (c) 1998, 2014, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ado/tender/TenderErrorCodeEnum.java /main/15 2014/04/14 15:54:36 abhinavs Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    abhinavs  03/19/14 - Fix to validate Mall certificate if it has already
 *                         been tendered out
 *    abondala  09/04/13 - initialize collections
 *    jswan     03/26/12 - Modified to support centralized gift certificate and
 *                         store credit.
 *    mkutiana  08/12/11 - adding enum to clarify validation code readability
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *    jswan     11/18/09 - Forward to fix use of gift cerificate more than once
 *                         in a transaction and making change to gift
 *                         certificate which already been redeemed.
 *    jswan     11/17/09 - XbranchMerge shagoyal_bug-8553074 from
 *                         rgbustores_13.0x_branch
 *
 * ===========================================================================
 * $Log:
 *    5    360Commerce 1.4         9/20/2007 12:09:12 PM  Rohit Sachdeva
 *         28813: Initial Bulk Migration for Java 5 Source/Binary
 *         Compatibility of All Products
 *    4    360Commerce 1.3         4/7/2006 4:59:41 AM    Akhilashwar K. Gupta
 *         CR-10596: Added new constant MIN_CHANGE_LIMIT_VIOLATED
 *    3    360Commerce 1.2         3/31/2005 4:30:23 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:25:57 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:14:51 PM  Robert Pearse   
 *
 *   Revision 1.8  2004/09/23 00:07:13  kmcbride
 *   @scr 7211: Adding static serialVersionUIDs to all POS Serializable objects, minus the JComponents
 *
 *   Revision 1.7  2004/07/17 21:14:34  jriggins
 *   @scr 6026 Added logic for checking to see if the transaction for an issued gift certificate has been post voided
 *
 *   Revision 1.6  2004/04/15 20:52:03  blj
 *   @scr 3872 - updated validation
 *
 *   Revision 1.5  2004/03/23 21:56:19  crain
 *   @scr 4082 Remove Enter Date flow
 *
 *   Revision 1.4  2004/03/04 23:06:03  nrao
 *   Added error codes as per Issue Store Credit code review.
 *
 *   Revision 1.3  2004/02/17 17:51:40  nrao
 *   Added error codes for Issue Store Credit.
 *
 *   Revision 1.2  2004/02/12 16:47:55  mcs
 *   Forcing head revision
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:11  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.5   Feb 05 2004 13:46:30   rhafernik
 * log4j changes
 * 
 *    Rev 1.4   Dec 01 2003 19:06:28   epd
 * Updates for Credit/Debit
 * 
 *    Rev 1.3   Nov 11 2003 15:42:50   crain
 * Added gift certificate errors
 * Resolution for 3421: Tender redesign
 * 
 *    Rev 1.2   Nov 10 2003 15:21:54   blj
 * added constants for Gift Card
 * 
 *    Rev 1.1   Nov 07 2003 13:23:16   bwf
 * Added invalid_license.
 * Resolution for 3429: Check/ECheck Tender
 * 
 *    Rev 1.0   Nov 04 2003 11:13:14   epd
 * Initial revision.
 * 
 *    Rev 1.3   Oct 27 2003 19:47:12   crain
 * Added error codes for coupon
 * Resolution for 3421: Tender redesign
 * 
 *    Rev 1.2   Oct 26 2003 14:23:38   blj
 * updated for money order tender
 * 
 *    Rev 1.1   Oct 24 2003 14:49:22   bwf
 * Added NO_CUSTOMER_LINKED
 * Resolution for 3418: Purchase Order Tender Refactor
 * 
 *    Rev 1.0   Oct 17 2003 12:33:46   epd
 * Initial revision.
 *   
 * ===========================================================================
 */
package oracle.retail.stores.pos.ado.tender;

import java.util.HashMap;

import oracle.retail.stores.pos.ado.utility.TypesafeEnumIfc;

/**
 *  Any time a TenderException is thrown, it should carry one
 *  of the error codes defines below.
 *     $Revision: /main/15 $
 */
public class TenderErrorCodeEnum implements TypesafeEnumIfc
{
    // This id is used to tell
    // the compiler not to generate a
    // new serialVersionUID.
    //
    static final long serialVersionUID = 7612141629023182032L;

    /** revision number **/
    public static final String revisionNumber = "$Revision: /main/15 $";
    
    /** The internal representation of an enumeration instance */
    private String enumer;

    /** The map containing the singleton enumeration instances */
    protected static final HashMap map = new HashMap(4);

    //////////////////////////////////////////////////////////////////
    // Put all definitions here (try to sort by category):
    // Note: It is legal to add new/shuffle categories if needed.

    // Tender Limit errors
    public static final TenderErrorCodeEnum MAX_LIMIT_VIOLATED      = new TenderErrorCodeEnum("MaxLimitViolated");
    public static final TenderErrorCodeEnum MIN_LIMIT_VIOLATED      = new TenderErrorCodeEnum("MinLimitViolated");
    public static final TenderErrorCodeEnum INVALID_AMOUNT          = new TenderErrorCodeEnum("InvalidAmount");
    public static final TenderErrorCodeEnum MAX_CHANGE_LIMIT_VIOLATED = new TenderErrorCodeEnum("MaxChangeLimitViolated");
    public static final TenderErrorCodeEnum MIN_CHANGE_LIMIT_VIOLATED = new TenderErrorCodeEnum("MinChangeLimitViolated");

    // Card related errors
    public static final TenderErrorCodeEnum UNKNOWN_CARD_TYPE       = new TenderErrorCodeEnum("UnknownCardType");
    public static final TenderErrorCodeEnum INVALID_CARD_TYPE       = new TenderErrorCodeEnum("InvalidCardType");
    public static final TenderErrorCodeEnum INVALID_CARD_NUMBER     = new TenderErrorCodeEnum("InvalidCardNumber");
    public static final TenderErrorCodeEnum BAD_MAG_SWIPE           = new TenderErrorCodeEnum("BadMadSwipe");

    // Travelers Check error
    public static final TenderErrorCodeEnum INVALID_QUANTITY        = new TenderErrorCodeEnum("InvalidQuantity");

    // Miscellaneous
    public static final TenderErrorCodeEnum EXPIRED                 = new TenderErrorCodeEnum("Expired");
    public static final TenderErrorCodeEnum INVALID_TENDER_TYPE     = new TenderErrorCodeEnum("InvalidTenderType");
    public static final TenderErrorCodeEnum INVALID_EXPIRATION_DATE = new TenderErrorCodeEnum("InvalidExpirationDate");
    public static final TenderErrorCodeEnum OVERTENDER_ILLEGAL      = new TenderErrorCodeEnum("OvertenderIllegal");
    public static final TenderErrorCodeEnum INVALID_NUMBER          = new TenderErrorCodeEnum("InvalidNumber");

    // Purchase Order error
    public static final TenderErrorCodeEnum NO_CUSTOMER_LINKED      = new TenderErrorCodeEnum("NoCustomerLinked");

    // Coupon error
    public static final TenderErrorCodeEnum MANUAL_INPUT            = new TenderErrorCodeEnum("ManualInput");
    public static final TenderErrorCodeEnum INVALID_COUPON          = new TenderErrorCodeEnum("InvalidCoupon");

    // Check error
    public static final TenderErrorCodeEnum INVALID_LICENSE         = new TenderErrorCodeEnum("InvalidLicense");
    public static final TenderErrorCodeEnum CHECK_MAX_OVERTENDER_LIMIT_VIOLATED = new TenderErrorCodeEnum("CheckMaxOvertenderLimitViolated");

    // Gift certificate error
    public static final TenderErrorCodeEnum CERTIFICATE_TENDERED    = new TenderErrorCodeEnum("CertificateTendered");
    public static final TenderErrorCodeEnum INVALID_CERTIFICATE     = new TenderErrorCodeEnum("InvalidCertificate");
    public static final TenderErrorCodeEnum CERTIFICATE_VOIDED      = new TenderErrorCodeEnum("CertificateVoided");
    public static final TenderErrorCodeEnum INVALID_CURRENCY        = new TenderErrorCodeEnum("InvalidCurrency");
    public static final TenderErrorCodeEnum GIFT_CERTIFICATE_NUMBER_ALREADY_USED = new TenderErrorCodeEnum("CertificateIssued");

    // Debit
    public static final TenderErrorCodeEnum DEBIT_NOT_SWIPED        = new TenderErrorCodeEnum("DebitNotSwipzed");

    // Issue Store Credit
    public static final TenderErrorCodeEnum CREATE_STORE_CREDIT     = new TenderErrorCodeEnum("CreateStoreCredit");
    public static final TenderErrorCodeEnum ENTER_STORE_CREDIT      = new TenderErrorCodeEnum("EnterStoreCredit");
    public static final TenderErrorCodeEnum NOT_ISSUE_STORE_CREDIT  = new TenderErrorCodeEnum("NotIssueStoreCredit");
    public static final TenderErrorCodeEnum ISSUE_STORE_CREDIT      = new TenderErrorCodeEnum("IssueStoreCredit");
    public static final TenderErrorCodeEnum VALIDATION_OFFLINE      = new TenderErrorCodeEnum("ValidationOffline");
    
    //Mall certificate validation error
    public static final TenderErrorCodeEnum MALL_CERTIFICATE_NUMBER_ALREADY_TENDERED = new TenderErrorCodeEnum("CertificateTendered");

    //////////////////////////////////////////////////////////////////

    protected TenderErrorCodeEnum(String errorCode)
    {
        this.enumer = errorCode;
        map.put(enumer, this);
    }

    /** get internal representation */
    public String toString()
    {
        return enumer;
    }

    /** factory method.  May return null */
    public static TenderErrorCodeEnum makeEnumFromString(String enumer)
    {
        return (TenderErrorCodeEnum)map.get(enumer);
    }

    /** fix deserialization 
     *
    */
    public Object readResolve()
    throws java.io.ObjectStreamException
    {
        return map.get(enumer);
    }
}
