/* ===========================================================================
* Copyright (c) 2008, 2013, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ado/store/RegisterMode.java /main/11 2013/09/05 10:36:15 abondala Exp $
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
 */
package oracle.retail.stores.pos.ado.store;

import java.util.HashMap;

import oracle.retail.stores.pos.ado.utility.TypesafeEnumIfc;

/**
 * @author epd
 * 
 * To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Generation - Code and Comments
 */
public class RegisterMode implements TypesafeEnumIfc
{
    // This id is used to tell
    // the compiler not to generate a
    // new serialVersionUID.
    //
    static final long serialVersionUID = 3289307198110414945L;

    /** The internal representation of an enumeration instance */
    private String enumer;

    /** The map containing the singleton enumeration instances */
    protected static final HashMap map = new HashMap(0);

    ///////////////////////////////////////////////////////////////
    // Register mode definitions
    public static final RegisterMode CASHIER_ACCOUNTABILITY =
        new RegisterMode("CashierAccountability");
    public static final RegisterMode REGISTER_ACCOUNTABILITY =
        new RegisterMode("RegisterAccountability");
    public static final RegisterMode TRAINING = new RegisterMode("Training");
    public static final RegisterMode REENTRY = new RegisterMode("Reentry");
    ///////////////////////////////////////////////////////////////

    /**
     * Constructor defers to super implementation for administration of new
     * enumeration entry.
     */
    protected RegisterMode(String modeName)
    {
        this.enumer = modeName;
        map.put(enumer, this);
    }

    /** get internal representation */
    public String toString()
    {
        return enumer;
    }

    /** factory method. May return null */
    public static RegisterMode makeEnumFromString(String enumer)
    {
        return (RegisterMode) map.get(enumer);
    }

    /** fix deserialization */
    public Object readResolve() throws java.io.ObjectStreamException
    {
        return map.get(enumer);
    }

}
