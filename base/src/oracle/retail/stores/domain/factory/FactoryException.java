/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/factory/FactoryException.java /rgbustores_13.4x_generic_branch/1 2011/05/04 11:48:48 mszekely Exp $
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
 *    3    360Commerce 1.2         3/31/2005 4:28:09 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:21:36 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:11:01 PM  Robert Pearse   
 *
 *   Revision 1.3  2004/09/23 00:30:53  kmcbride
 *   @scr 7211: Inserting serialVersionUIDs in these Serializable classes
 *
 *   Revision 1.2  2004/06/03 14:47:36  epd
 *   @scr 5368 Update to use of DataTransactionFactory
 *
 *   Revision 1.1  2004/04/01 20:07:37  epd
 *   @scr 4243 Updates for new Database Transaction Factory
 *
 *   
 * ===========================================================================
 */
package oracle.retail.stores.domain.factory;


/**
 * This exception should be thrown when factory methods cannot produce
 * a requested object instance.
 */
public class FactoryException extends RuntimeException
{
    // This id is used to tell
    // the compiler not to generate a
    // new serialVersionUID.
    //
    static final long serialVersionUID = 4879325457233046565L;

    /**
     * Constructor simply forwards to superclass implementation.
     */
    public FactoryException()
    {
        super();
    }
    
    /**
     * Constructor simply forwards to superclass implementation.
     * @param msg
     */
    public FactoryException(String msg)
    {
        super(msg);
    }
    
    /**
     * Constructor simply forwards to superclass implementation.
     * @param msg
     * @param originalException
     */
    public FactoryException(String msg, Throwable originalException)
    {
        super(msg, originalException);
    }
    
    /**
     * Constructor simply forwards to superclass implementation.
     * @param originalException
     */
    public FactoryException(Throwable originalException)
    {
        super(originalException);
    }
}
