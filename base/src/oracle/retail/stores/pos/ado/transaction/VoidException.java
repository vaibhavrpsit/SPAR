/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ado/transaction/VoidException.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:05:41 mszekely Exp $
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
 *    3    360Commerce 1.2         3/31/2005 4:30:46 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:26:46 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:15:33 PM  Robert Pearse   
 *
 *   Revision 1.3  2004/09/23 00:07:18  kmcbride
 *   @scr 7211: Adding static serialVersionUIDs to all POS Serializable objects, minus the JComponents
 *
 *   Revision 1.2  2004/02/12 16:47:57  mcs
 *   Forcing head revision
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:11  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.1   Feb 05 2004 13:22:58   rhafernik
 * log4j conversion
 * 
 *    Rev 1.0   Nov 04 2003 11:14:40   epd
 * Initial revision.
 * 
 *    Rev 1.0   Oct 17 2003 12:35:24   epd
 * Initial revision.
 *   
 * ===========================================================================
 */
package oracle.retail.stores.pos.ado.transaction;

import oracle.retail.stores.foundation.utility.BaseException;

/**
 *
 * 
 */
public class VoidException extends BaseException
{
    // This id is used to tell
    // the compiler not to generate a
    // new serialVersionUID.
    //
    static final long serialVersionUID = 1046369625009784270L;

    /** 
     * This error code provides the reason for the exception
     * and is intended to be used by Service code to determine
     * control flow.
     */
    protected final VoidErrorCodeEnum errorCode;

    
    public VoidException(String msg, VoidErrorCodeEnum errorCode)
    {
        super(msg);
        this.errorCode = errorCode;
    }
    
    public VoidException(String msg, VoidErrorCodeEnum errorCode, Throwable throwable)
    {
        super(msg, throwable);
        this.errorCode = errorCode;
    }
    
    
    public VoidErrorCodeEnum getErrorCode()
    {
        return errorCode;
    }
}
