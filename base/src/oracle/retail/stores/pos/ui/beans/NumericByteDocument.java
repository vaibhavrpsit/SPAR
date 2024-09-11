/* ===========================================================================
* Copyright (c) 2007, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/NumericByteDocument.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:51 mszekely Exp $
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
 *  4    360Commerce 1.3         2/1/2008 10:36:41 AM   Alan N. Sinton  CR
 *       30106: Fixed backspacing issue on NumericByteTextField.
 *  3    360Commerce 1.2         12/27/2007 10:39:29 AM Alan N. Sinton  CR
 *       29677: Check in changes per code review.  Reviews are Michael Barnett
 *        and Tony Zgarba.
 *  2    360Commerce 1.1         11/29/2007 5:15:58 PM  Alan N. Sinton  CR
 *       29677: Protect user entry fields of PAN data.
 *  1    360Commerce 1.0         11/13/2007 2:40:38 PM  Jack G. Swan    Added
 *       to support retrieving card numbers from UI as a byte array instead of
 *        a String object.
 * $
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.beans;

import javax.swing.text.BadLocationException;


//-------------------------------------------------------------------------
/**
   This document allows input to be valid if it meets max and min length
   requirements and is numeric.

   @version $KW=@(#); $Ver=pos_4.5.0:2; $EKW;
*/
//-------------------------------------------------------------------------
class NumericByteDocument extends NumericDocument implements BytesRetrievableIfc
{
    /**  */
    private static final long serialVersionUID = 3687010632418952869L;
    
    /** revision number supplied by Team Connection */
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    //---------------------------------------------------------------------
    /**
       Constructor.
    */
    //---------------------------------------------------------------------
    public NumericByteDocument()
    {
        this(Integer.MAX_VALUE);
    }

    //---------------------------------------------------------------------
    /**
       Constructor.
       @param maxLength the maximum length
    */
    //---------------------------------------------------------------------
    public NumericByteDocument(int maxLength)
    {
        super(maxLength, new BytesContent());
    }

    //---------------------------------------------------------------------
    /**
       Gets the entered text as bytes
       @return byte array
    */
    //---------------------------------------------------------------------
    public byte[] getTextBytes()
    {
        byte[] array = null;
        if (getContent() instanceof BytesContent)
        {
            try 
            {
                array = ((BytesContent)getContent()).getTextBytes(0, getLength());
            } 
            catch (BadLocationException e) 
            {
            }
        }
        return array;
    }

    //---------------------------------------------------------------------
    /**
       Sets the text as bytes
       @param byte array
    */
    //---------------------------------------------------------------------
    public void setTextBytes(byte[] value)
    {
        if (value != null)
        {
            boolean numeric = true;
            int len = value.length;
            for (int i=0; (i < len) && numeric ; ++i)
            {
                if (!Character.isDigit((char)value[i]))
                {
                    numeric = false;
                }
            }
            if (numeric)
            {
                byte[] b = new byte[1];
                for(int i = 0; i < len; i++)
                {
                    b[0] = value[i];
                    String text = new String(b);
                    try
                    {
                        insertSingleByteString(i, text, null);
                    }
                    catch(BadLocationException e)
                    {
                    }
                }
            }
        }
    }

    //---------------------------------------------------------------------
    /**
       Clears the content of currently held values.
    */
    //---------------------------------------------------------------------
    public void clearTextBytes()
    {
        if (getContent() instanceof BytesContent)
        {
            ((BytesContent)getContent()).clearTextBytes();
        }
    }

    //---------------------------------------------------------------------
    /**
     * Overrides super's getText method so that we can construct the desired
     * String for the given offset and length from the internal storage bytes.
     * A limit of 4 is placed on the length of the resulting string.  If the
     * length argument is greater than 4, then a BadLocationException is
     * thrown.
     * 
     * This method is being overriden in order to prevent a
     * StringIndexOutOfBoundsException in DefaultEditorKit when text is being
     * deleted by backspacing in the NumericByteTextField.
     * 
     * @param offset
     * @param length
     * @return The substring from the internal storage for the given offset
     * and length.
     * @throws BadLocationException 
     */
    //---------------------------------------------------------------------
    public String getText(int offset, int length) throws BadLocationException
    {
        String returnValue = "";
        byte[] text = getTextBytes();
        if(text != null)
        {
            // limit the length of the string to 4
            if(( offset + length > text.length ) || ( length > 4 ))
            {
                throw new BadLocationException("Invalid range", offset + length);
            }
            byte[] outText = new byte[length];
            System.arraycopy(text, offset, outText, 0, length);
            returnValue = new String(outText);
        }
        return returnValue; 
    }
}
