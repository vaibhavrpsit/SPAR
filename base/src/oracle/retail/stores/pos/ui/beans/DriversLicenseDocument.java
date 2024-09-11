/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/DriversLicenseDocument.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:43 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/28/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 * 5    360Commerce 1.4         10/15/2007 9:45:45 AM  Anda D. Cadar   updates
 * 4    360Commerce 1.3         10/10/2007 1:02:00 PM  Anda D. Cadar   Changes
 *      to not allow double byte chars
 * 3    360Commerce 1.2         3/31/2005 4:27:51 PM   Robert Pearse   
 * 2    360Commerce 1.1         3/10/2005 10:21:10 AM  Robert Pearse   
 * 1    360Commerce 1.0         2/11/2005 12:10:43 PM  Robert Pearse   
 *
 *Revision 1.4  2004/09/23 00:07:11  kmcbride
 *@scr 7211: Adding static serialVersionUIDs to all POS Serializable objects, minus the JComponents
 *
 *Revision 1.3  2004/03/16 17:15:22  build
 *Forcing head revision
 *
 *Revision 1.2  2004/03/16 17:15:17  build
 *Forcing head revision
 *
 *Revision 1.1.1.1  2004/02/11 01:04:22  cschellenger
 *updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 16:10:14   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.0   Apr 24 2003 14:41:50   bwf
 * Initial revision.
 * Resolution for 2208: Space and Asterisk chars are not allowed in a driver's license ID number
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.beans;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
//------------------------------------------------------------------------------
/**
 * This document allows input to be valid if it meets max and min length
 * requirements and is alpha numeric or contains '*' or ' '.  The alpha characters 
 * will be converted to uppercase.
 * @version $Revision: /rgbustores_13.4x_generic_branch/1 $
 */
//------------------------------------------------------------------------------
class DriversLicenseDocument extends ConstrainedTextDocument
{
    // This id is used to tell
    // the compiler not to generate a
    // new serialVersionUID.
    //
    static final long serialVersionUID = 3336158315196954967L;

    /** Revision number */
    public static String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";
    //---------------------------------------------------------------------
    /**
       Constructor.
    */
    //---------------------------------------------------------------------
    public DriversLicenseDocument()
    {
        this(Integer.MAX_VALUE);
    }
    
    //---------------------------------------------------------------------
    /**
       Constructor.
       @param maxLength the maximum length
    */
    //---------------------------------------------------------------------
    public DriversLicenseDocument(int maxLength)
    {
        super(maxLength);
    }
    
    //  ---------------------------------------------------------------------
    /**
        Determines if the text can be inserted.
        @param offset the offset at which the text should be inserted
        @param text the text to be inserted
        @param attributes the set of attributes for the text
        @exception BadLocationException if the offset is invalid
    */
    //---------------------------------------------------------------------
    public void insertString(int offset, String text, AttributeSet attributes)
           throws BadLocationException
    {
        if (text != null)
        {
        	if(doubleByteCharsAllowed)
        	{
	            boolean alphaNumeric = true;
	            char[] buf = text.toCharArray();
	            for (int i=0; (i < buf.length) && alphaNumeric ; ++i)
	            {
	                if (!Character.isLetterOrDigit(buf[i]))
	                {
	                    if(buf[i] != '*' &&
	                       buf[i] != ' ')
	                       {
	                           alphaNumeric = false;
	                       }
	                }
	            }
	            if (alphaNumeric)
	            {
	                super.insertStringWithoutCharValidation(offset, new String(buf), attributes);
	            }
        	}
        	else
        	{
        		//insert only single byte strings
        		insertSingleByteString(offset, text, attributes);
        	}
            
        }
    }
    
  //---------------------------------------------------------------------
    /**
       Allows only single byte  text to be inserted.
       @param offset the offset at which the text should be inserted
       @param text the text to be inserted
       @param attributes the set of attributes for the text
       @exception BadLocationException if the offset is invalid
    */
    //---------------------------------------------------------------------
    public void insertSingleByteString(int offset, String text, AttributeSet attributes)
        throws BadLocationException
    {
    	
            boolean singleByte = true;
            char[] buf = text.toCharArray();
            for (int i=0; (i < buf.length) && singleByte ; ++i)
            {
                if (!(Character.UnicodeBlock.of(buf[i]).equals(Character.UnicodeBlock.BASIC_LATIN))) 
            	{
                	singleByte = false;
            	}
                else
                {
                	int asciiValue = (int)buf[i];
                	if ((asciiValue >= 0 && asciiValue <= 31)||
                		(asciiValue >= 33 && asciiValue <= 41)||
                	    (asciiValue >= 43 && asciiValue <=47) ||
                	    (asciiValue >= 58 && asciiValue <=64) ||
                	    (asciiValue >= 91 && asciiValue <= 96) ||
                	    (asciiValue >= 123 && asciiValue <= 255))
                	    
                	 {
                       singleByte = false;
                     }
               }
                
               
             }
            if (singleByte)
            {
                super.insertStringWithoutCharValidation(offset, new String(buf), attributes);
            }
        
    }

}
