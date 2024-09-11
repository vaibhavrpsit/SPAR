/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ado/ADOException.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:05:41 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.ado;

import oracle.retail.stores.foundation.utility.BaseException;

/**
 * Base Exception class for ADO operations
 */
public class ADOException extends BaseException 
                  implements ErrorInfoIfc
{
    // This id is used to tell
    // the compiler not to generate a
    // new serialVersionUID.
    //
    static final long serialVersionUID = 1828921295037100575L;

    private String errorTextDefault;
    private String errorTextResourceName;

    /**
     * @param arg0
     */
    public ADOException(String arg0)
    {
        super(arg0);
    }

    /**
     * @param arg0
     */
    public ADOException(Throwable arg0)
    {
        super(arg0);
    }

    /**
    * @param arg0
    * @param arg1
    */
    public ADOException(String arg0, Throwable arg1)
    {
       super(arg0, arg1);
    }

    /**
    Returns the error text default. <P>
    @return The error text default
    **/
    public String getErrorTextDefault()
    {
       return errorTextDefault;
    }

    /**
    Sets the error text default. <P>
    @param int the error text default
    **/
    public void setErrorTextDefault(String errorTextDefault)
    {
       this.errorTextDefault = errorTextDefault;
    }

    /**
    Returns the error text resource name. <P>
    @return The error text resource name
    **/
    public String getErrorTextResourceName()
    {
       return errorTextResourceName;
    }    

    /**
    Sets the error text resource name. <P>
    @param int the error text resource name
    **/
    public void setErrorTextResourceName(String errorTextResourceName)
    {
       this.errorTextResourceName = errorTextResourceName;
    }
}
