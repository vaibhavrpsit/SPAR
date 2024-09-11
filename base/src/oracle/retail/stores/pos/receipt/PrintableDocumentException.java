/* ===========================================================================
* Copyright (c) 2007, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/receipt/PrintableDocumentException.java /rgbustores_13.4x_generic_branch/2 2011/06/03 09:46:42 cgreene Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   06/02/11 - Tweaks to support Servebase chipnpin
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *  1    360Commerce 1.0         4/30/2007 7:00:39 PM   Alan N. Sinton  CR
 *       26485 - Merge from v12.0_temp.
 * $
 * ===========================================================================
 */
package oracle.retail.stores.pos.receipt;

/**
 * Exception class for PrintableDocument error handling.
 * $Revision: /rgbustores_13.4x_generic_branch/2 $
 */
public class PrintableDocumentException extends Exception
{
    private static final long serialVersionUID = 95296096L;
    /**
     * Nested exception reference.
     * @deprecated as of 13.4. Use {@link #getCause()} instead.
     */
    protected Throwable nested = null;
    /**
     *
     * @param arg0
     */
    public PrintableDocumentException(String arg0)
    {
        super(arg0);
    }

    /**
     *
     * @param arg0
     * @param arg1
     */
    public PrintableDocumentException(String arg0, Throwable arg1)
    {
        super(arg0, arg1);
        this.nested = arg1;
    }

    /**
     * Returns the nested exception.
     * @return The nested exception.
     * @deprecated as of 13.4. Use {@link #getCause()} instead.
     */
    public Throwable getNestedException()
    {
        return getCause();
    }
}
