/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/manager/tax/TaxCalculationException.java /rgbustores_13.4x_generic_branch/1 2011/05/04 11:48:52 mszekely Exp $
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
 *    3    360Commerce 1.2         3/31/2005 4:30:18 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:25:46 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:14:41 PM  Robert Pearse   
 *
 *   Revision 1.4  2004/09/23 00:30:55  kmcbride
 *   @scr 7211: Inserting serialVersionUIDs in these Serializable classes
 *
 *   Revision 1.3  2004/02/12 17:14:15  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 23:26:58  bwf
 *   @scr 0 Organize imports.
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:33  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 15:38:32   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.0   Jun 03 2002 16:59:38   msg
 * Initial revision.
 * 
 *    Rev 1.1   Mar 18 2002 23:05:50   msg
 * - updated copyright
 * 
 *    Rev 1.0   Mar 18 2002 12:25:20   msg
 * Initial revision.
 * 
 *    Rev 1.0   Sep 20 2001 16:16:54   msg
 * Initial revision.
 * 
 *    Rev 1.1   Sep 17 2001 12:38:22   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.domain.manager.tax;


//------------------------------------------------------------------------
/**
    Exception thrown by the tax calculation classes on configuration or
    calculation exceptions
*/
//------------------------------------------------------------------------
public class TaxCalculationException extends java.lang.Exception
{
    // This id is used to tell
    // the compiler not to generate a
    // new serialVersionUID.
    //
    static final long serialVersionUID = -2616229306563463940L;

    /**
       revision number supplied by Team Connection
    **/
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    /**
        Error code constants
    */
    public final static int UNKNOWN = 0;
    public final static int TAX_CALC_NOT_FOUND = 1;
    public final static int TAX_GROUP_NOT_FOUND = 2;
    public final static int EXCEPTION_DURING_CALC = 3;
    public final static int INVALID_CONFIGURATION_NODE = 4;
    public final static int INVALID_CONFIGURATION_INFO = 5;
    public final static int INVALID_CLASS_NAME = 6;
    public final static int RATE_CREATION_EXCEPTION = 7;
    
    /**
        Major error code of the exception
    */
    protected int code = UNKNOWN;
    
    /**
        Message
    */
    protected String message;
    
    //--------------------------------------------------------------------
    /**
        Create a new instance of the TaxCalculationException
        @param code - major exception code 
        @param message - explanation of the exception
     */
    //--------------------------------------------------------------------
    public TaxCalculationException(int code, String message)
    {
        this.code = code;
        this.message = message;
    }
    
    //--------------------------------------------------------------------
    /**
        Return the message
        @return detailed message of the exception
    */
    //--------------------------------------------------------------------
    public String getMessage()
    {
        return message;
    }
    
    //--------------------------------------------------------------------
    /**
        return the error code
        @return error code of the exception
    */
    //--------------------------------------------------------------------
    public int getCode()
    {
        return code;
    }
    
        
    //----------------------------------------------------------------------
    /**
        Retrieves the Team Connection revision number.
        <p>
        @return String representation of revision number
    **/
    //----------------------------------------------------------------------
    public static String getRevisionNumber()
    {                                   // begin getRevisionNumber()
        return(revisionNumber);
    }                                   // end getRevisionNumber()

    //----------------------------------------------------------------------
    /**
        Returns a human readable string representation of this object.
        <p>
        @return A human readable string representation of this object.
    **/
    //----------------------------------------------------------------------
    public String toString()
    {
        // The string to be returned
        String strResult = "Class:  " + getClass().getName() +
            "(Revision " + getRevisionNumber() + ")@" + hashCode();
        
        strResult +="\nError Code: " + this.code;
            strResult += "\nMessage:" + message;
        return strResult;
    }
        
}
