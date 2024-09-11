/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/modifyitem/tax/TaxException.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:24 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *  3    360Commerce 1.2         3/31/2005 4:30:19 PM   Robert Pearse   
 *  2    360Commerce 1.1         3/10/2005 10:25:46 AM  Robert Pearse   
 *  1    360Commerce 1.0         2/11/2005 12:14:41 PM  Robert Pearse   
 * $
 * Revision 1.5  2004/09/23 00:07:11  kmcbride
 * @scr 7211: Adding static serialVersionUIDs to all POS Serializable objects, minus the JComponents
 *
 * Revision 1.4  2004/03/16 18:30:41  cdb
 * @scr 0 Removed tabs from all java source code.
 *
 * Revision 1.3  2004/03/12 20:36:39  bjosserand
 * @scr 3954 Tax Override
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.modifyitem.tax;

import oracle.retail.stores.pos.ado.ErrorInfoIfc;
import oracle.retail.stores.foundation.utility.BaseException;

public class TaxException extends BaseException implements ErrorInfoIfc
{
    // This id is used to tell
    // the compiler not to generate a
    // new serialVersionUID.
    //
    static final long serialVersionUID = 2657609544462758207L;

    private String errorTextKey;
    private String errorTextDefault;
    private String errorTextResourceName;

    /**
     * Constructor with String
     * 
     * @param arg0
     */
    public TaxException(String arg0)
    {
        super(arg0);
    }

    /**
     * Constructor with Throwable
     * 
     * @param arg0
     */
    public TaxException(Throwable arg0)
    {
        super(arg0);
    }

    /**
     * Constructor with String and Throwable
     * 
     * @param arg0
     * @param arg1
     */
    public TaxException(String arg0, Throwable arg1)
    {
        super(arg0, arg1);
    }

    /**
     * Returns the error text string. Used for error dialogs.
     * <P>
     * 
     * @return The error text
     */
    public String getErrorTextKey()
    {
        return errorTextKey;
    }

    /**
     * Sets the error text string. Used for error dialogs.
     * <P>
     * 
     * @param String
     *            the error text
     */
    public void setErrorTextKey(String errorTextKey)
    {
        this.errorTextKey = errorTextKey;
    }

    /**
     * Returns the error text default. Used for error dialogs.
     * <P>
     * 
     * @return The error text default
     */
    public String getErrorTextDefault()
    {
        return errorTextDefault;
    }

    /**
     * Sets the error text default. Used for error dialogs.
     * <P>
     * 
     * @param int
     *            the error text default
     */
    public void setErrorTextDefault(String errorTextDefault)
    {
        this.errorTextDefault = errorTextDefault;
    }

    /**
     * Returns the error text resource name. Used for error dialogs.
     * <P>
     * 
     * @return The error text resource name
     */
    public String getErrorTextResourceName()
    {
        return errorTextResourceName;
    }

    /**
     * Sets the error text resource name. Used for error dialogs.
     * <P>
     * 
     * @param int
     *            the error text resource name
     */
    public void setErrorTextResourceName(String errorTextResourceName)
    {
        this.errorTextResourceName = errorTextResourceName;
    }
}
