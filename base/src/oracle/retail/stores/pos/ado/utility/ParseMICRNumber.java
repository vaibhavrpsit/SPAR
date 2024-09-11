/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ado/utility/ParseMICRNumber.java /rgbustores_13.4x_generic_branch/2 2011/07/20 04:33:44 rrkohli Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    rrkohli   07/19/11 - encryption CR
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:29:19 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:24:00 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:13:00 PM  Robert Pearse   
 *
 *   Revision 1.3  2004/02/17 18:36:03  epd
 *   @scr 0
 *   Code cleanup. Returned unused local variables.
 *
 *   Revision 1.2  2004/02/12 16:47:58  mcs
 *   Forcing head revision
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:11  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.1   Dec 09 2003 10:04:44   bwf
 * Updated per code review.
 * 
 *    Rev 1.0   Nov 07 2003 16:25:28   bwf
 * Initial revision.
 * Resolution for 3429: Check/ECheck Tender
 * ===========================================================================
 */
package oracle.retail.stores.pos.ado.utility;

import oracle.retail.stores.common.utility.Util;

//--------------------------------------------------------------------------
/**
    This class parses MICR numbers.
    $Revision: /rgbustores_13.4x_generic_branch/2 $
**/
//--------------------------------------------------------------------------
public class ParseMICRNumber
{
    /** revision **/    
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/2 $";

    /**
        MICR number
    **/
    private String micrNumber = "";
    
    private String ABANumber = "";
    private String accountNumber = "";
    
    //---------------------------------------------------------------------
    /**                     
       Constructor. 
    */
    //---------------------------------------------------------------------
    public ParseMICRNumber(String value) 
    {
        micrNumber = value;
        parseData();
    }
    
    //---------------------------------------------------------------------
    /**                     
       Constructor. 
    */
    //---------------------------------------------------------------------
    public ParseMICRNumber(byte[] value) 
    {
       this(new String(value));
       Util.flushByteArray(value);
        
    }
    
    //---------------------------------------------------------------------
    /**
        Retrieves check transit number.  <P>
        @return check transit number.
    */
    //---------------------------------------------------------------------
    public String getTransitNumber()
    {
        return ABANumber;
    }
    
    //---------------------------------------------------------------------
    /**
        Retrieves check transit number byte array.  <P>
        @return check transit number byte array.
    */
    //---------------------------------------------------------------------
    public byte[] getTransitNumberByte()
    {
        return ABANumber.getBytes();
    }
    //---------------------------------------------------------------------
    /**
        Retrieves check account number.  <P>
        @return check account number.
    */
    //---------------------------------------------------------------------
    public String getAccountNumber()
    {
        return accountNumber;
    }
    
    //---------------------------------------------------------------------
    /**
        Retrieves check account number.  <P>
        @return check account number.
    */
    //---------------------------------------------------------------------
    public byte[] getAccountNumberByte()
    {
        return accountNumber.getBytes();
    }
    
    //---------------------------------------------------------------------
    /**
        Parses check MICR data.  <P>
    */
    //---------------------------------------------------------------------
    protected void parseData()
    {
        if ((micrNumber != null) &&
            (micrNumber.length() >= 10))
        {
           try
           {
               ABANumber = micrNumber.substring(0, 9);
               accountNumber = micrNumber.substring(9, micrNumber.length());
           }
           catch (IndexOutOfBoundsException e)
           {
               reset();
           }           
        }
    }
    
    //---------------------------------------------------------------------
    /**
        Resets all the fields present in check MICR.  <P>
    */
    //---------------------------------------------------------------------
    private void reset()
    {
        ABANumber = "";
        accountNumber = "";
    }
    
    //---------------------------------------------------------------------
    /**
        Method to default display string function. <P>
        @return String representation of object
    */
    //---------------------------------------------------------------------
    public String toString()
    {
        String strResult = new String("Class:  ParseMICRNumber " +
                                      hashCode() + "\n");
                                      
        strResult += "Transit Number:                         [" + ABANumber + "]\n";
        strResult += "Account Number:                         [" + accountNumber + "]\n";

        return strResult;
    }
}
