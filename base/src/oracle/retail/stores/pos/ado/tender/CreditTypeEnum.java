/* ===========================================================================
* Copyright (c) 1998, 2013, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ado/tender/CreditTypeEnum.java /main/12 2013/09/05 10:36:15 abondala Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    abondala  09/04/13 - initialize collections
 *    ohorne    08/15/11 - created CardTypeCodesIfc
 *    cgreene   07/12/11 - add non-case sensitive searching
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    4    360Commerce 1.3         9/20/2007 12:09:12 PM  Rohit Sachdeva
 *         28813: Initial Bulk Migration for Java 5 Source/Binary
 *         Compatibility of All Products
 *    3    360Commerce 1.2         3/31/2005 4:27:33 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:20:27 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:10:15 PM  Robert Pearse   
 *
 *   Revision 1.3.2.1  2004/10/25 17:44:29  kmcbride
 *   @scr 7384
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
 *    Rev 1.1   Feb 05 2004 13:46:18   rhafernik
 * log4j changes
 * 
 *    Rev 1.0   Nov 04 2003 11:13:10   epd
 * Initial revision.
 * 
 *    Rev 1.2   Oct 27 2003 18:47:12   epd
 * fixed credit logic
 * 
 *    Rev 1.1   Oct 22 2003 19:15:46   epd
 * Added JCB
 * 
 *    Rev 1.0   Oct 17 2003 12:33:42   epd
 * Initial revision.
 *   
 * ===========================================================================
 */
package oracle.retail.stores.pos.ado.tender;

import java.util.HashMap;

import oracle.retail.stores.domain.utility.CardTypeCodesIfc;
import oracle.retail.stores.pos.ado.utility.TypesafeEnumIfc;

/**
 * Defines all known credit types
 */
public class CreditTypeEnum implements TypesafeEnumIfc
{
    // This id is used to tell the compiler not to generate a new serialVersionUID.
    static final long serialVersionUID = 3752331142738665673L;

    /** The internal representation of an enumeration instance */
    private String enumer;
    
    /** The map containing the singleton enumeration instances */
    protected static final HashMap<String,CreditTypeEnum> map = new HashMap<String,CreditTypeEnum>(0);
    
    ////////////////////////////////////////////////////////////////////////////
    // Put all definitions here
    public static final CreditTypeEnum VISA       = new CreditTypeEnum(CardTypeCodesIfc.VISA);
    public static final CreditTypeEnum MASTERCARD = new CreditTypeEnum(CardTypeCodesIfc.MASTER_CARD);
    public static final CreditTypeEnum AMEX       = new CreditTypeEnum(CardTypeCodesIfc.AMEX);
    public static final CreditTypeEnum DISCOVER   = new CreditTypeEnum(CardTypeCodesIfc.DISCOVER);
    public static final CreditTypeEnum DINERS     = new CreditTypeEnum(CardTypeCodesIfc.DINERS_CLUB);
    public static final CreditTypeEnum HOUSECARD  = new CreditTypeEnum(CardTypeCodesIfc.HOUSE_CARD);
    public static final CreditTypeEnum JCB        = new CreditTypeEnum(CardTypeCodesIfc.JCB);
    // definition for cards for which we cannot determine the type
    public static final CreditTypeEnum UNKNOWN    = new CreditTypeEnum(CardTypeCodesIfc.UNKNOWN);
    ////////////////////////////////////////////////////////////////////////////
    
    protected CreditTypeEnum(String creditType)
    {
        this.enumer = creditType;
        map.put(enumer, this);
    }
    
    /** get internal representation */
    public String toString()
    {
        return enumer;
    }
    
    /** factory method.  Will return UNKNOWN if not in the list */
    public static CreditTypeEnum makeEnumFromString(String enumer)
    {
        CreditTypeEnum result = map.get(enumer);
        if (result == null)
        {
            // try non-case-sensitive search
            for (String name : map.keySet())
            {
                if (enumer.equalsIgnoreCase(name))
                {
                    return map.get(name);
                }
            }
            // no results - return unknown enum
            result = UNKNOWN;
        }

        return result;
    }
    
    /** fix deserialization */
    public Object readResolve()
    throws java.io.ObjectStreamException
    {
        return map.get(enumer);
    }
    
    

}
