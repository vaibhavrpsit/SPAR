/* ===========================================================================
* Copyright (c) 1998, 2013, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ado/tender/AuthResponseCodeEnum.java /main/13 2013/09/05 10:36:15 abondala Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    abondala  09/04/13 - initialize collections
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *    asinton   03/02/09 - Fix for Partially approved credit.
 *
 * ===========================================================================
 * $Log:
 |    6    360Commerce 1.5         4/28/2008 12:12:53 PM  Charles D. Baker CR
 |         30905 - Corrected loss of file history in header.
 |    5    360Commerce 1.4         4/28/2008 5:03:25 AM   Deepankar Ghosh Added
 |          a constant ZERO_BALANCE used when tendering an Item with gift card
 |          which has zero balance
 |    4    360Commerce 1.3         9/20/2007 12:09:12 PM  Rohit Sachdeva
 |         28813: Initial Bulk Migration for Java 5 Source/Binary
 |         Compatibility of All Products
 |    3    360Commerce 1.2         3/31/2005 4:27:15 PM   Robert Pearse   
 |    2    360Commerce 1.1         3/10/2005 10:19:47 AM  Robert Pearse   
 |    1    360Commerce 1.0         2/11/2005 12:09:33 PM  Robert Pearse   
 |   $
 |
 |   Revision 1.4  2004/09/23 00:07:13  kmcbride
 |   @scr 7211: Adding static serialVersionUIDs to all POS Serializable objects, minus the JComponents
 |
 |   Revision 1.3  2004/02/25 18:32:52  bwf
 |   @scr 3883 Credit Rework.
 |
 |   Revision 1.2  2004/02/12 16:47:55  mcs
 |   Forcing head revision
 |
 |   Revision 1.1.1.1  2004/02/11 01:04:11  cschellenger
 |   updating to pvcs 360store-current
 | 
 |    Rev 1.2   Dec 04 2003 16:52:32   epd
 | Updates for debit auth
 | 
 |    Rev 1.1   Nov 04 2003 18:23:46   epd
 | updates for auth
 | 
 |    Rev 1.0   Nov 04 2003 11:13:10   epd
 | Initial revision.
 | 
 |    Rev 1.0   Oct 30 2003 12:58:56   epd
 | Initial revision.
 * ===========================================================================
 */
package oracle.retail.stores.pos.ado.tender;

import java.util.HashMap;

import oracle.retail.stores.pos.ado.utility.TypesafeEnumIfc;

/**
 * Defines all known auth response codes
 */
public class AuthResponseCodeEnum implements TypesafeEnumIfc
{
    // This id is used to tell
    // the compiler not to generate a
    // new serialVersionUID.
    //
    static final long serialVersionUID = 3437595221524583826L;

    /** The internal representation of an enumeration instance */
    private String enumer;
    
    /** The map containing the singleton enumeration instances */
    protected static final HashMap<String, AuthResponseCodeEnum> map = new HashMap<String, AuthResponseCodeEnum>(0);
    
    
    ////////////////////////////////////////////////////////////////////////////
    // Put all defintions here
    public static final AuthResponseCodeEnum APPROVED           = new AuthResponseCodeEnum("Approved");
    public static final AuthResponseCodeEnum PARTIALLY_APPROVED = new AuthResponseCodeEnum("PartiallyApproved");
    public static final AuthResponseCodeEnum DECLINED           = new AuthResponseCodeEnum("Declined");
    public static final AuthResponseCodeEnum POSITIVE_ID        = new AuthResponseCodeEnum("PositiveID");
    public static final AuthResponseCodeEnum REFERRAL           = new AuthResponseCodeEnum("Referral");
    public static final AuthResponseCodeEnum TIMEOUT            = new AuthResponseCodeEnum("Timeout");
    public static final AuthResponseCodeEnum OFFLINE            = new AuthResponseCodeEnum("Offline");
    public static final AuthResponseCodeEnum ERROR_RETRY        = new AuthResponseCodeEnum("ErrorRetry");
    public static final AuthResponseCodeEnum INVALID_PIN        = new AuthResponseCodeEnum("InvalidPin");
    public static final AuthResponseCodeEnum MAX_PIN_ATTEMPTS   = new AuthResponseCodeEnum("MaxPINAttempts");
    public static final AuthResponseCodeEnum INVALID_MERCH_CALL = new AuthResponseCodeEnum("InvalidMerchCall");
    public static final AuthResponseCodeEnum FIRST_TIME_USAGE   = new AuthResponseCodeEnum("FirstTimeUsage");
    public static final AuthResponseCodeEnum ZERO_BALANCE   	= new AuthResponseCodeEnum("ZeroBalance");
    ////////////////////////////////////////////////////////////////////////////
    
    private AuthResponseCodeEnum(String responseCode)
    {
        this.enumer = responseCode;
        map.put(enumer, this);
    }
    
    /** get internal representation */
    public String toString()
    {
        return enumer;
    }
    
    /** factory method.  Will return null if not in the list */
    public static AuthResponseCodeEnum makeEnumFromString(String enumer)
    {
        AuthResponseCodeEnum result = null;
        result = (AuthResponseCodeEnum)map.get(enumer);
        return result;
    }
    
    /** fix deserialization */
    public Object readResolve()
    throws java.io.ObjectStreamException
    {
        return map.get(enumer);
    }
    
    

}
