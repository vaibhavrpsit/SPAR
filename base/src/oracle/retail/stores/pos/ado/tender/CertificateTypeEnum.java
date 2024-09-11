/* ===========================================================================
* Copyright (c) 1998, 2013, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ado/tender/CertificateTypeEnum.java /main/12 2013/09/05 10:36:15 abondala Exp $
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
 *    3    360Commerce 1.2         3/31/2005 4:27:22 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:20:02 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:09:51 PM  Robert Pearse   
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
 *    Rev 1.3   Feb 05 2004 13:46:14   rhafernik
 * log4j changes
 * 
 *    Rev 1.2   Dec 07 2003 18:48:08   crain
 * Added foreign type
 * Resolution for 3421: Tender redesign
 * 
 *    Rev 1.1   Dec 03 2003 17:22:34   crain
 * Added foreign type
 * Resolution for 3421: Tender redesign
 * 
 *    Rev 1.0   Nov 20 2003 15:40:14   crain
 * Initial revision.
 * Resolution for 3421: Tender redesign
 * 
 *   
 * ===========================================================================
 */
package oracle.retail.stores.pos.ado.tender;

import java.util.HashMap;

import oracle.retail.stores.pos.ado.utility.TypesafeEnumIfc;
import oracle.retail.stores.domain.tender.TenderLineItemIfc;


/**
 *    This class enumerates the certificate types
 */
public class CertificateTypeEnum implements TypesafeEnumIfc
{
    // This id is used to tell
    // the compiler not to generate a
    // new serialVersionUID.
    //
    static final long serialVersionUID = -2376943016670485945L;

    /** The internal representation of an enumeration instance */
    private String enumer;
    
    /** The map containing the singleton enumeration instances */
    protected static final HashMap map = new HashMap(0);
    
    /////////////////////////////////////////////////////////////////
    // Certificate definitions
    /////////////////////////////////////////////////////////////////
    public static final CertificateTypeEnum STORE             = new CertificateTypeEnum(TenderLineItemIfc.CERTIFICATE_TYPE_STORE); 
    public static final CertificateTypeEnum CORPORATE         = new CertificateTypeEnum(TenderLineItemIfc.CERTIFICATE_TYPE_CORPORATE);
    public static final CertificateTypeEnum FOREIGN           = new CertificateTypeEnum(TenderLineItemIfc.CERTIFICATE_TYPE_FOREIGN);
    /////////////////////////////////////////////////////////////////
    
    
    protected CertificateTypeEnum(String certificateName)
    {
        this.enumer = certificateName;
        map.put(enumer, this);
    }
    
    /** get internal representation */
    public String toString()
    {
        return enumer;
    }
    
    /** factory method.  May return null */
    public static CertificateTypeEnum makeEnumFromString(String enumer)
    {
        return (CertificateTypeEnum)map.get(enumer);
    }
    
    /** fix deserialization */
    public Object readResolve()
    throws java.io.ObjectStreamException
    {
        return map.get(enumer);
    }
}
