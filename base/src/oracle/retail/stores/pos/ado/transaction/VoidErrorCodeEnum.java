/* ===========================================================================
* Copyright (c) 1998, 2013, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ado/transaction/VoidErrorCodeEnum.java /main/13 2013/09/05 10:36:15 abondala Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    abondala  09/04/13 - initialize collections
 *    jswan     03/21/12 - Modified to support centralized gift certificate and
 *                         store credit.
 *    asinton   10/04/11 - prevent post voiding of transactions with authorized
 *                         tenders that lack necessary data for reversing.
 *    asinton   09/06/11 - remove ability to post void transactions with issue,
 *                         reload, redeem of gift cards.
 *    asinton   05/09/11 - Removed restrictions for voiding of gift cards
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *    asinton   03/18/09 - Modified to show correct dialog.
 *
 * ===========================================================================
 * $Log:
 *    4    360Commerce 1.3         9/20/2007 12:09:12 PM  Rohit Sachdeva
 *         28813: Initial Bulk Migration for Java 5 Source/Binary
 *         Compatibility of All Products
 *    3    360Commerce 1.2         3/31/2005 4:30:46 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:26:46 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:15:33 PM  Robert Pearse   
 *
 *   Revision 1.3  2004/09/23 00:07:18  kmcbride
 *   @scr 7211: Adding static serialVersionUIDs to all POS Serializable objects, minus the JComponents
 *
 *   Revision 1.2  2004/02/12 16:47:57  mcs
 *   Forcing head revision
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:11  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.2   Feb 05 2004 13:22:56   rhafernik
 * log4j conversion
 * 
 *    Rev 1.1   Jan 22 2004 15:19:54   epd
 * updated void dialogs
 * 
 *    Rev 1.0   Nov 04 2003 11:14:38   epd
 * Initial revision.
 * 
 *    Rev 1.0   Oct 17 2003 12:35:24   epd
 * Initial revision.
 *   
 * ===========================================================================
 */
package oracle.retail.stores.pos.ado.transaction;

import java.util.HashMap;

import oracle.retail.stores.pos.ado.utility.TypesafeEnumIfc;

/**
 * Enumeration of post void error codes.
 * 
 */
public class VoidErrorCodeEnum implements TypesafeEnumIfc
{
    // This id is used to tell
    // the compiler not to generate a
    // new serialVersionUID.
    //
    static final long serialVersionUID = -9161945420535353966L;

    /** The internal representation of an enumeration instance */
    private String enumer;
    
    /** The map containing the singleton enumeration instances */
    protected static final HashMap<String, VoidErrorCodeEnum> map = new HashMap<String, VoidErrorCodeEnum>(0);
    
    ///////////////////////////////////////////////////////////////////
    // Put definitions here
    ///////////////////////////////////////////////////////////////////
    public static final VoidErrorCodeEnum NOT_FOUND             = new VoidErrorCodeEnum("NotFound");
    public static final VoidErrorCodeEnum DIFFERENT_TILL         = new VoidErrorCodeEnum("DifferentTill");
    public static final VoidErrorCodeEnum PREVIOUSLY_VOIDED     = new VoidErrorCodeEnum("PreviouslyVoided");
    public static final VoidErrorCodeEnum INVALID_TRANSACTION     = new VoidErrorCodeEnum("InvalidTransaction");
    public static final VoidErrorCodeEnum TRANSACTION_MODIFIED     = new VoidErrorCodeEnum("TransactionModified");
    /** error code for post voiding a redeem, reload, or issue of gift card */
    public static final VoidErrorCodeEnum GIFT_CARD_VOID_INVALID = new VoidErrorCodeEnum("GiftCardVoidInvalid");
    /** error code for post voiding a redeem, reload, or issue of gift card */
    public static final VoidErrorCodeEnum GIFT_CERTIFICATE_VOID_INVALID = new VoidErrorCodeEnum("GiftCertificateVoidInvalid");
    /** error code for post voiding a redeem, reload, or issue of gift card */
    public static final VoidErrorCodeEnum STORE_CREDIT_VOID_INVALID = new VoidErrorCodeEnum("StoreCreditVoidInvalid");
    /** error code for post voiding a transaction where the gift card that has been modified since. */
    public static final VoidErrorCodeEnum VOID_GIFT_CARD_INVALID = new VoidErrorCodeEnum("VoidGiftCardInvalid");

    /** Authorized Tender not voidable */
    public static final VoidErrorCodeEnum AUTH_TENDER_NOT_VOIDABLE = new VoidErrorCodeEnum("AuthTenderNotVoidable");
    ///////////////////////////////////////////////////////////////////
    
    protected VoidErrorCodeEnum(String errorCode)
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
    public static VoidErrorCodeEnum makeEnumFromString(String enumer)
    {
        return (VoidErrorCodeEnum)map.get(enumer);
    }
    
    /** fix deserialization */
    public Object readResolve()
    throws java.io.ObjectStreamException
    {
        return map.get(enumer);
    }
}
