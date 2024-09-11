/* ===========================================================================
* Copyright (c) 1998, 2013, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ado/transaction/TenderStateEnum.java /main/11 2013/09/05 10:36:15 abondala Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    abondala  09/04/13 - initialize collections
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    4    360Commerce 1.3         9/20/2007 12:09:12 PM  Rohit Sachdeva
 *         28813: Initial Bulk Migration for Java 5 Source/Binary
 *         Compatibility of All Products
 *    3    360Commerce 1.2         3/31/2005 4:30:26 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:26:03 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:14:57 PM  Robert Pearse   
 *
 *   Revision 1.5  2004/09/23 00:07:18  kmcbride
 *   @scr 7211: Adding static serialVersionUIDs to all POS Serializable objects, minus the JComponents
 *
 *   Revision 1.4  2004/03/26 21:18:19  cdb
 *   @scr 4204 Removing Tabs.
 *
 *   Revision 1.3  2004/03/22 17:26:43  blj
 *   @scr 3872 - added redeem security, receipt printing and saving redeem transactions.
 *
 *   Revision 1.2  2004/02/12 16:47:57  mcs
 *   Forcing head revision
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:11  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Nov 04 2003 11:14:36   epd
 * Initial revision.
 * 
 *    Rev 1.0   Oct 17 2003 12:35:22   epd
 * Initial revision.
 *   
 * ===========================================================================
 */
package oracle.retail.stores.pos.ado.transaction;

import java.util.HashMap;

import oracle.retail.stores.pos.ado.utility.TypesafeEnumIfc;

/**
 *  This class enumerates the possible tendering states a transaction has.
 *  When tendering, the state of the transaction is evaluated to determine
 *  whether change is due, etc.  Each condition is enumerated here.
 */
public class TenderStateEnum implements TypesafeEnumIfc
{
    // This id is used to tell
    // the compiler not to generate a
    // new serialVersionUID.
    //
    static final long serialVersionUID = 5134981021902764369L;

    /** The internal representation of an enumeration instance */
    private String enumer;
    
    /** The map containing the singleton enumeration instances */
    protected static final HashMap map = new HashMap(0);
    
    /////////////////////////////////////////////////////////////////
    // Tender definitions
    /////////////////////////////////////////////////////////////////
    public static final TenderStateEnum TENDER_OPTIONS          = new TenderStateEnum("TenderOptions");
    public static final TenderStateEnum REFUND_OPTIONS          = new TenderStateEnum("RefundOptions");
    public static final TenderStateEnum REDEEM_REFUND_OPTIONS     = new TenderStateEnum("RedeemRefundOptions");
    public static final TenderStateEnum CHANGE_DUE              = new TenderStateEnum("ChangeDue");
    public static final TenderStateEnum REFUND_DUE              = new TenderStateEnum("RefundDue");
    public static final TenderStateEnum PAID_UP                 = new TenderStateEnum("PaidUp");
    /////////////////////////////////////////////////////////////////
    
    
    protected TenderStateEnum(String tenderState)
    {
        this.enumer = tenderState;
        map.put(enumer, this);
    }
    
    /** get internal representation */
    public String toString()
    {
        return enumer;
    }
    
    /** factory method.  May return null */
    public static TenderStateEnum makeEnumFromString(String enumer)
    {
        return (TenderStateEnum)map.get(enumer);
    }
    
    /** fix deserialization */
    public Object readResolve()
    throws java.io.ObjectStreamException
    {
        return map.get(enumer);
    }
}
