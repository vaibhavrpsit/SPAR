/* ===========================================================================
* Copyright (c) 1998, 2013, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ado/tender/CouponTypeEnum.java /main/12 2013/09/05 10:36:15 abondala Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    abondala  09/04/13 - initialize collections
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    4    360Commerce 1.3         9/20/2007 12:09:12 PM  Rohit Sachdeva
 *         28813: Initial Bulk Migration for Java 5 Source/Binary
 *         Compatibility of All Products
 *    3    360Commerce 1.2         3/31/2005 4:27:32 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:20:25 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:10:13 PM  Robert Pearse   
 *
 *   Revision 1.3  2004/09/23 00:07:13  kmcbride
 *   @scr 7211: Adding static serialVersionUIDs to all POS Serializable objects, minus the JComponents
 *
 *   Revision 1.2  2004/02/12 16:47:55  mcs
 *   Forcing head revision
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:11  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.1   Feb 05 2004 13:46:16   rhafernik
 * log4j changes
 * 
 *    Rev 1.0   Nov 04 2003 11:13:08   epd
 * Initial revision.
 * 
 *    Rev 1.0   Oct 30 2003 12:55:12   crain
 * Initial revision.
 * ===========================================================================
 */
package oracle.retail.stores.pos.ado.tender;

import java.util.HashMap;

import oracle.retail.stores.pos.ado.utility.TypesafeEnumIfc;

/**
 *    This class enumerates all the coupon types
 */
public class CouponTypeEnum implements TypesafeEnumIfc
{
    // This id is used to tell
    // the compiler not to generate a
    // new serialVersionUID.
    //
    static final long serialVersionUID = -3518044253336254859L;

    /** The internal representation of an enumeration instance */
    private String enumer;
    
    /** The map containing the singleton enumeration instances */
    protected static final HashMap map = new HashMap(0);
    
    /** Coupon type descriptor */
    protected String couponTypeDescriptor;
    
    /** Coupon type code */
    protected String couponTypeCode;
    
    /** Coupon type as integer for legacy reasons */
    protected int couponTypeRDO;
    
    /////////////////////////////////////////////////////////////////
    // Coupon types
    /////////////////////////////////////////////////////////////////
    public static final CouponTypeEnum COUPON_TYPE_UNDEFINED     = new CouponTypeEnum("CouponTypeUndefined", "Undefined", "", -1); 
    public static final CouponTypeEnum COUPON_TYPE_MANUFACTURER  = new CouponTypeEnum("CouponTypeManufacturer", "Manufacturer", "MF", 0);
    public static final CouponTypeEnum COUPON_TYPE_STORE         = new CouponTypeEnum("CouponTypeStore", "Store", "ST", 1);
    public static final CouponTypeEnum COUPON_TYPE_ELECTRONIC    = new CouponTypeEnum("CouponTypeElectronic", "Electronic", "EL", 2);
        
    /////////////////////////////////////////////////////////////////
    
    
    protected CouponTypeEnum(String couponType, String couponTypeDescriptor, String couponTypeCode, int couponTypeRDO)
    {
        this.enumer = couponType;
        this.couponTypeDescriptor = couponTypeDescriptor;
        this.couponTypeCode = couponTypeCode;
        this.couponTypeRDO = couponTypeRDO;
        map.put(enumer, this);
    }
    
    /** get internal representation */
    public String toString()
    {
        return enumer;
    }

    /** return type as integer */
    public int getCouponTypeRDO()
    {
        return makeEnumFromString(enumer).couponTypeRDO;
    }

    /** return type description */
    public String getCouponTypeDescriptor ()
    {
        return makeEnumFromString(enumer).couponTypeDescriptor;
    }
    
    /** return type code */
    public String getCouponTypeCode ()
    {
        return makeEnumFromString(enumer).couponTypeCode;
    }
    
    /** factory method.  May return null */
    public static CouponTypeEnum makeEnumFromString(String enumer)
    {
        return (CouponTypeEnum)map.get(enumer);
    }
    
    /** fix deserialization */
    public Object readResolve()
    throws java.io.ObjectStreamException
    {
        return map.get(enumer);
    }
}
