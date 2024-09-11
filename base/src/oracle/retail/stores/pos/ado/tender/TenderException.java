/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ado/tender/TenderException.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:05:43 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:30:23 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:25:57 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:14:51 PM  Robert Pearse   
 *
 *   Revision 1.5  2004/09/23 00:07:13  kmcbride
 *   @scr 7211: Adding static serialVersionUIDs to all POS Serializable objects, minus the JComponents
 *
 *   Revision 1.4  2004/05/10 19:08:08  crain
 *   @scr 4182 Able to use a Issued Gift Cert over and over, Tender Redeemed never appears
 *
 *   Revision 1.3  2004/05/05 23:28:04  crain
 *   @scr 4182 Able to use a Issued Gift Cert over and over, Tender Redeemed never appears
 *
 *   Revision 1.2  2004/02/12 16:47:55  mcs
 *   Forcing head revision
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:11  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Nov 04 2003 11:13:14   epd
 * Initial revision.
 * 
 *    Rev 1.0   Oct 17 2003 12:33:48   epd
 * Initial revision.
 *        
 * ===========================================================================
 */
package oracle.retail.stores.pos.ado.tender;

import oracle.retail.stores.foundation.utility.BaseException;

/**
 * This exception is thrown when an attempt is made to 
 * add a tender to a transaction, but it turns out
 * not to be possible.
 */
public class TenderException extends BaseException
{
    // This id is used to tell
    // the compiler not to generate a
    // new serialVersionUID.
    //
    static final long serialVersionUID = -3187599801124121672L;

    /** 
     * This error code provides the reason for the exception
     * and is intended to be used by Service code to determine
     * control flow.
     */
    protected final TenderErrorCodeEnum errorCode;
    /**
     * This is needed when we send objects through RMI and they get changed 
     */
    protected Object changedObject;

    public TenderException(String msg, TenderErrorCodeEnum errorCode)
    {
        super(msg);
        this.errorCode = errorCode;
    }
    
    public TenderException(String msg, TenderErrorCodeEnum errorCode, Throwable throwable)
    {
        super(msg, throwable);
        this.errorCode = errorCode;
    }
    
    /** 
     * Constructor
     * @param msg String
     * @param errorCode TenderErrorCodeEnum
     * @param value Object
     */
    public TenderException(String msg, TenderErrorCodeEnum errorCode, Object value)
    {
        super(msg);
        this.errorCode = errorCode;
        changedObject = value;
    }
    
    public TenderErrorCodeEnum getErrorCode()
    {
        return errorCode;
    }

    /** 
     * Get the changed attributes
     * @return Object
     */
    public Object getChangedObject()
    {
        return changedObject;
    }
    
}
