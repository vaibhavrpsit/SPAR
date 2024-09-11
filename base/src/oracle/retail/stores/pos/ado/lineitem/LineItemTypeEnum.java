/* ===========================================================================
* Copyright (c) 1998, 2013, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ado/lineitem/LineItemTypeEnum.java /main/11 2013/09/05 10:36:15 abondala Exp $
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
 *  3    360Commerce 1.2         3/31/2005 4:28:51 PM   Robert Pearse   
 *  2    360Commerce 1.1         3/10/2005 10:23:06 AM  Robert Pearse   
 *  1    360Commerce 1.0         2/11/2005 12:12:19 PM  Robert Pearse   
 *
 * Revision 1.4  2004/09/23 00:07:11  kmcbride
 * @scr 7211: Adding static serialVersionUIDs to all POS Serializable objects, minus the JComponents
 *
 * Revision 1.3  2004/04/08 20:33:02  cdb
 * @scr 4206 Cleaned up class headers for logs and revisions.
 *
 * 
 * Rev 1.0 Nov 04 2003 11:11:44 epd Initial revision.
 * 
 * Rev 1.0 Oct 17 2003 12:31:58 epd Initial revision.
 * ===========================================================================
 */
package oracle.retail.stores.pos.ado.lineitem;

import java.util.HashMap;

import oracle.retail.stores.pos.ado.utility.TypesafeEnumIfc;
import oracle.retail.stores.domain.stock.ProductGroupConstantsIfc;

/**
 * Enumerate Line Item types
 *  
 */
public class LineItemTypeEnum implements TypesafeEnumIfc
{
    // This id is used to tell
    // the compiler not to generate a
    // new serialVersionUID.
    //
    static final long serialVersionUID = -4073361212140466464L;

    /** The internal representation of an enumeration instance */
    private String enumer;

    /** The map containing the singleton enumeration instances */
    protected static final HashMap map = new HashMap(0);

    ///////////////////////////////////////////////////////////////
    // Register mode definitions
    public static final LineItemTypeEnum TYPE_GIFT_CARD =
        new LineItemTypeEnum(ProductGroupConstantsIfc.PRODUCT_GROUP_GIFT_CARD);
    ///////////////////////////////////////////////////////////////

    /**
     * Constructor defers to super implementation for administration of new
     * enumeration entry.
     */
    protected LineItemTypeEnum(String lineItemGroupType)
    {
        this.enumer = lineItemGroupType;
        map.put(enumer, this);
    }

    /** get internal representation */
    public String toString()
    {
        return enumer;
    }

    /** factory method. May return null */
    public static LineItemTypeEnum makeEnumFromString(String enumer)
    {
        return (LineItemTypeEnum) map.get(enumer);
    }

    /** fix deserialization */
    public Object readResolve() throws java.io.ObjectStreamException
    {
        return map.get(enumer);
    }

}
