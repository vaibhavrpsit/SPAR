/* ===========================================================================
* Copyright (c) 1998, 2013, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ado/journal/JournalFamilyEnum.java /main/11 2013/09/05 10:36:15 abondala Exp $
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
 *  3    360Commerce 1.2         3/31/2005 4:28:48 PM   Robert Pearse   
 *  2    360Commerce 1.1         3/10/2005 10:22:57 AM  Robert Pearse   
 *  1    360Commerce 1.0         2/11/2005 12:12:10 PM  Robert Pearse   
 *
 * Revision 1.4  2004/09/23 00:07:17  kmcbride
 * @scr 7211: Adding static serialVersionUIDs to all POS Serializable objects, minus the JComponents
 *
 * Revision 1.3  2004/04/08 20:33:02  cdb
 * @scr 4206 Cleaned up class headers for logs and revisions.
 *
 * 
 * Rev 1.0 Nov 04 2003 11:11:12 epd Initial revision.
 * 
 * Rev 1.0 Oct 17 2003 12:31:20 epd Initial revision.
 * ===========================================================================
 */
package oracle.retail.stores.pos.ado.journal;

import java.util.HashMap;

import oracle.retail.stores.pos.ado.utility.TypesafeEnumIfc;

/**
 *  
 */
public class JournalFamilyEnum implements TypesafeEnumIfc
{
    // This id is used to tell
    // the compiler not to generate a
    // new serialVersionUID.
    //
    static final long serialVersionUID = -6270216166595784510L;

    /** The internal representation of an enumeration instance */
    private String enumer;

    /** The map containing the singleton enumeration instances */
    protected static final HashMap map = new HashMap(0);

    ////////////////////////////////////////////////////////////////////////////
    // Put all defintions here
    public static final JournalFamilyEnum TRANSACTION =
        new JournalFamilyEnum("Transaction");
    public static final JournalFamilyEnum LINEITEM =
        new JournalFamilyEnum("LineItem");
    public static final JournalFamilyEnum TENDER =
        new JournalFamilyEnum("Tender");
    ////////////////////////////////////////////////////////////////////////////

    private JournalFamilyEnum(String family)
    {
        this.enumer = family;
        map.put(enumer, this);
    }

    /** get internal representation */
    public String toString()
    {
        return enumer;
    }

    /** factory method. May return null */
    public static JournalFamilyEnum makeEnumFromString(String enumer)
    {
        return (JournalFamilyEnum) map.get(enumer);
    }

    /** fix deserialization */
    public Object readResolve() throws java.io.ObjectStreamException
    {
        return map.get(enumer);
    }

    /**
     * Since the internal representation of this enumeration is a String,
     * simply use String's hashCode() method.
     */
    public int hashCode()
    {
        return enumer.hashCode();
    }
}
