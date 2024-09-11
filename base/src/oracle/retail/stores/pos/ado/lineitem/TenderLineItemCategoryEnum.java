/* ===========================================================================
* Copyright (c) 1998, 2013, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ado/lineitem/TenderLineItemCategoryEnum.java /main/11 2013/09/05 10:36:15 abondala Exp $
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
 *  4    360Commerce 1.3         9/20/2007 12:09:12 PM  Rohit Sachdeva  28813:
 *       Initial Bulk Migration for Java 5 Source/Binary Compatibility of All
 *       Products
 *  3    360Commerce 1.2         3/31/2005 4:30:25 PM   Robert Pearse   
 *  2    360Commerce 1.1         3/10/2005 10:26:01 AM  Robert Pearse   
 *  1    360Commerce 1.0         2/11/2005 12:14:55 PM  Robert Pearse   
 *
 * Revision 1.7  2004/09/23 00:07:11  kmcbride
 * @scr 7211: Adding static serialVersionUIDs to all POS Serializable objects, minus the JComponents
 *
 * Revision 1.6  2004/07/14 21:21:55  jriggins
 * @scr 4401 Added logic to determine when to capture customer info during tender
 *
 * Revision 1.5  2004/04/15 22:03:38  epd
 * @scr 4322 Updates for Tender Invariant work: handling Change invariant
 *
 * Revision 1.4  2004/04/14 22:37:54  epd
 * @scr 4322 Tender Invariant work.  Specifically for change invariant
 *
 * Revision 1.3  2004/04/08 20:33:02  cdb
 * @scr 4206 Cleaned up class headers for logs and revisions.
 *
 * 
 * Rev 1.2 Nov 14 2003 16:44:06 epd added new category
 * 
 * Rev 1.1 Nov 11 2003 16:18:12 epd Updates made to accommodate tender
 * deletion/reversal
 * 
 * Rev 1.0 Nov 04 2003 11:11:44 epd Initial revision.
 * 
 * Rev 1.0 Oct 31 2003 09:00:40 epd Initial revision.
 * 
 * Rev 1.0 Oct 31 2003 08:58:04 epd Initial revision.
 * ===========================================================================
 */
package oracle.retail.stores.pos.ado.lineitem;

import java.util.HashMap;

import oracle.retail.stores.pos.ado.utility.TypesafeEnumIfc;

/**
 * Lists categories of retrievable groups of tenders from the transaction
 */
public class TenderLineItemCategoryEnum implements TypesafeEnumIfc
{
    // This id is used to tell
    // the compiler not to generate a
    // new serialVersionUID.
    //
    static final long serialVersionUID = 149600559822727338L;

    /** The internal representation of an enumeration instance */
    private String enumer;

    /** The map containing the singleton enumeration instances */
    protected static final HashMap map = new HashMap(0);

    /////////////////////////////////////////////////////////////////
    // Category definitions
    /////////////////////////////////////////////////////////////////
    // Used to retrieve all the tender line items on the transaction
    public static final TenderLineItemCategoryEnum ALL =
        new TenderLineItemCategoryEnum("All");
    // Used to retrieve the tender line items for which authorization is still
    // required.
    public static final TenderLineItemCategoryEnum AUTH_PENDING =
        new TenderLineItemCategoryEnum("AuthPending");
    // Used to retrieve the tender line items for which authorization
    // has occurred, reversal is required to remove, and reversal has
    // not yet occurred.
    public static final TenderLineItemCategoryEnum REVERSAL_PENDING =
        new TenderLineItemCategoryEnum("ReversalPending");
    // Used to retrieve tenders line items that are being voided for which
    // authorization is still required
    public static final TenderLineItemCategoryEnum VOID_AUTH_PENDING =
        new TenderLineItemCategoryEnum("VoidAuthPending");
    // Used to retrieve forced cash change tender
    public static final TenderLineItemCategoryEnum FORCED_CASH_CHANGE =
        new TenderLineItemCategoryEnum("ForcedCashChange");
    // Positive tenders are those that are being payed to the store by the customer.
    public static final TenderLineItemCategoryEnum POSITIVE_TENDERS =
        new TenderLineItemCategoryEnum("PositiveTenders");
    // Negative tenders are those that are being payed to the store by the customer.
    public static final TenderLineItemCategoryEnum NEGATIVE_TENDERS =
        new TenderLineItemCategoryEnum("NegativeTenders");    
    /////////////////////////////////////////////////////////////////

    protected TenderLineItemCategoryEnum(String category)
    {
        this.enumer = category;
        map.put(enumer, this);
    }

    /** get internal representation */
    public String toString()
    {
        return enumer;
    }

    /** factory method. May return null */
    public static TenderLineItemCategoryEnum makeEnumFromString(String enumer)
    {
        return (TenderLineItemCategoryEnum) map.get(enumer);
    }

    /** fix deserialization */
    public Object readResolve() throws java.io.ObjectStreamException
    {
        return map.get(enumer);
    }
}
