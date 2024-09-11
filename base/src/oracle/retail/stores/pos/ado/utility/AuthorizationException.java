/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ado/utility/AuthorizationException.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:05:41 mszekely Exp $
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
 *    3    360Commerce 1.2         3/31/2005 4:27:15 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:19:45 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:09:32 PM  Robert Pearse   
 *
 *   Revision 1.3  2004/09/23 00:07:17  kmcbride
 *   @scr 7211: Adding static serialVersionUIDs to all POS Serializable objects, minus the JComponents
 *
 *   Revision 1.2  2004/02/12 16:47:58  mcs
 *   Forcing head revision
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:11  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Nov 04 2003 11:15:08   epd
 * Initial revision.
 * 
 *    Rev 1.2   Oct 30 2003 20:42:06   epd
 * updates
 * 
 *    Rev 1.1   Oct 29 2003 15:30:28   epd
 * updates
 * 
 *    Rev 1.0   Oct 17 2003 12:39:06   epd
 * Initial revision.
 *   
 * ===========================================================================
 */
package oracle.retail.stores.pos.ado.utility;

import oracle.retail.stores.pos.ado.tender.AuthResponseCodeEnum;
import oracle.retail.stores.foundation.utility.BaseException;

/**
 *  Thrown when a tender fails authorization
 * 
 */
public class AuthorizationException extends BaseException
{
    // This id is used to tell
    // the compiler not to generate a
    // new serialVersionUID.
    //
    static final long serialVersionUID = -3447928663268480659L;

    /* The response code */
    protected AuthResponseCodeEnum responseCode;
    /* The response display */
    protected String responseDisplay;
    
    public AuthorizationException(String msg, AuthResponseCodeEnum responseCode, String responseDisplay)
    {
        super(msg);
        this.responseCode = responseCode;
        this.responseDisplay = responseDisplay;
    }
    
    /**
     * @return
     */
    public AuthResponseCodeEnum getResponseCode()
    {
        return responseCode;
    }

    /**
     * @return
     */
    public String getResponseDisplay()
    {
        return responseDisplay;
    }

}
