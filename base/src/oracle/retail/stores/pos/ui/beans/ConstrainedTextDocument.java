/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/ConstrainedTextDocument.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:54 mszekely Exp $
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
 *   8    360Commerce 1.7         2/4/2008 12:09:49 PM   Charles D. Baker CR
 *        29652 - Repaired ConstrainedTextField to permit insertString
 *        override of insertString in implementing classes. Code reviewed by
 *        Sandy Gu.
 *   7    360Commerce 1.6         11/13/2007 2:38:51 PM  Jack G. Swan
 *        Modified to support retrieving a byte array from the UI for card
 *        numbers instead of a String object. 
 *   6    360Commerce 1.5         10/11/2007 3:46:55 PM  Maisa De Camargo Added
 *         MaxLength validation to insertStringWithoutCharValidation() method
 *        and filtering characters with accent in the insertSingleByteString()
 *         method.
 *   5    360Commerce 1.4         10/10/2007 1:02:52 PM  Anda D. Cadar
 *        changes to not allow double byte chars
 *   4    360Commerce 1.3         10/9/2007 11:37:13 AM  Maisa De Camargo Added
 *         methods to allow a TextField to be set without checking the
 *        maxLength. These methods mimic the WebApp behavior.
 *   3    360Commerce 1.2         3/31/2005 4:27:30 PM   Robert Pearse   
 *   2    360Commerce 1.1         3/10/2005 10:20:23 AM  Robert Pearse   
 *   1    360Commerce 1.0         2/11/2005 12:10:11 PM  Robert Pearse   
 *
 *  Revision 1.6  2004/09/23 00:07:11  kmcbride
 *  @scr 7211: Adding static serialVersionUIDs to all POS Serializable objects, minus the JComponents
 *
 *  Revision 1.5  2004/04/09 13:59:07  cdb
 *  @scr 4206 Cleaned up class headers for logs and revisions.
 *
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.beans;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

//-------------------------------------------------------------------------
/**
   This field allows input to be valid if it meets max and min length
   requirements.
   @version $KW=@(#); $Ver=pos_4.5.0:2; $EKW;
*/
//-------------------------------------------------------------------------
public class ConstrainedTextDocument extends PlainDocument
{
    // This id is used to tell
    // the compiler not to generate a
    // new serialVersionUID.
    //
    static final long serialVersionUID = -8742880851610269838L;

    /** revision number supplied by version control */
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    /** Allows for temporary bypass of max length restriction **/
    public boolean transientBypassMaxValidation = false;
    
    /** the maximum length of the field */
    protected int maxLength;
    
    /** flag that indicates if double byte chars are allowed */
    protected boolean doubleByteCharsAllowed = true;

    /** @deprecated As of release 13.0, No replacement */
    public static final String DEFAULT_NEG_PREFIX = ")";

    /** @deprecated As of release 13.0, No replacement */
    public static final String DEFAULT_NEG_SUFFIX = "(";

    //---------------------------------------------------------------------
    /**
       Constructor.
       @param maxLength the maximum length
    */
    //---------------------------------------------------------------------
    public ConstrainedTextDocument(int maxLength)
    {
        super();
        this.maxLength = maxLength;
    }
    
    //---------------------------------------------------------------------
    /**
       Constructor.
       @param maxLength the maximum length
       @param content the container for the content 
    */
    //---------------------------------------------------------------------
    public ConstrainedTextDocument(int maxLength, Content content)
    {
        super(content);
        this.maxLength = maxLength;
    }
    
    //---------------------------------------------------------------------
    /**
       Constructor.
       @param maxLength the maximum length
       @param doubleByteCharsAllowed indicates if double byte chars are allowed
    */
    //---------------------------------------------------------------------
    public ConstrainedTextDocument(int maxLength, boolean doubleByteCharsAllowed)
    {
        super();
        this.maxLength = maxLength;
        this.doubleByteCharsAllowed = doubleByteCharsAllowed;
    }

    //---------------------------------------------------------------------
    /**
       Constructor.
       @param maxLength the maximum length
       @param doubleByteCharsAllowed indicates if double byte chars are allowed
       @param content the container for the content 
    */
    //---------------------------------------------------------------------
    public ConstrainedTextDocument(int maxLength, boolean doubleByteCharsAllowed, Content content)
    {
        super(content);
        this.maxLength = maxLength;
        this.doubleByteCharsAllowed = doubleByteCharsAllowed;
    }

    //---------------------------------------------------------------------
    /**
       Returns the maximum length of a valid document.
       @return the maximum length of a valid document
    */
    //---------------------------------------------------------------------
    public int getMaxLength()
    {
        return maxLength;
    }

    /**
       Determines if the text can be inserted based on the text and maxLength
       
       @param offset the offset at which the text should be inserted
       @param text the text to be inserted
       @param attributes the set of attributes for the text
       @exception BadLocationException if the offset is invalid
    */
    //---------------------------------------------------------------------
    public void insertString(int offset, String text, AttributeSet attributes)
        throws BadLocationException
    {
        if(doubleByteCharsAllowed)
        {
	    	insertUTF8String(offset, text, attributes);
        }
        else
        {
        	insertSingleByteString(offset, text, attributes);
        }
    }
    
    /**
  
    This method should only be called by subclasses of ConstrainedTextValidation
    that has their own validation.
    @param offset the offset at which the text should be inserted
    @param text the text to be inserted
    @param attributes the set of attributes for the text
    @exception BadLocationException if the offset is invalid
    */
    //---------------------------------------------------------------------
    protected void insertStringWithoutCharValidation(int offset, String text, AttributeSet attributes)
     throws BadLocationException
   {
        try
        {
            if (text != null && (transientBypassMaxValidation ||
                                (text.length() + getLength() <= maxLength)))
            {
                super.insertString(offset, text, attributes);
            }
        }
        finally
        {
            transientBypassMaxValidation = false;
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
    public void insertUTF8String(int offset, String text, AttributeSet attributes)
        throws BadLocationException
    {
        try
        {
            if (text != null && (transientBypassMaxValidation ||
                                 (text.length() + getLength() <= maxLength)))
            {
                super.insertString(offset, text, attributes);
            }
        }
        finally
        {
            transientBypassMaxValidation = false;
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
    public void insertSingleByteString(int offset, String text, AttributeSet attributes) throws BadLocationException
    {
        try
        {
            if (text != null && (transientBypassMaxValidation ||
                                 (text.length() + getLength() <= maxLength)))
            {
                boolean singleByte = true;
                char[] buf = text.toCharArray();
                for (int i = 0; (i < buf.length) && singleByte; ++i)
                {
                    if (!(Character.UnicodeBlock.of(buf[i]).equals(Character.UnicodeBlock.BASIC_LATIN)))
                    {
                        singleByte = false;
                    }
                    else
                    {
                        // eliminate the accented character from the set - they are
                        // double bytes
                        int asciiValue = buf[i];
                        if ((asciiValue >= 127 && asciiValue <= 255) || (asciiValue >= 0 && asciiValue <= 31))
                        {
                            singleByte = false;
                        }
                    }
                }

                if (singleByte)
                {
                    super.insertString(offset, new String(buf), attributes);
                }
            }
        }
        finally
        {
            transientBypassMaxValidation = false;
        }
    }


    // ---------------------------------------------------------------------
    /**
       Sets the maximum length of a valid document.
       @param maxLength the maximum length for a valid document
    */
    //---------------------------------------------------------------------
    public void setMaxLength(int maxLength)
    {
        this.maxLength = maxLength;
    }

	/**
	 * returns boolean
	 * @return if double byte chars are allowed
	 */
    public boolean isDoubleByteCharsAllowed() {
		return doubleByteCharsAllowed;
	}

	/**
	 * Sets the double byte char flag
	 * @param doubleByteCharsAllowed if double byte chars are allowed
	 */
    public void setDoubleByteCharsAllowed(boolean doubleByteCharsAllowed) {
		this.doubleByteCharsAllowed = doubleByteCharsAllowed;
	}

    /** 
     * Allows for temporary bypass of max length restriction. This is to allow a temporary
     * bypass of the max length validation so that the application can insert values
     * longer than allowed a by a user.
     * 
     * The JTextComponent.setText method is used during programatic setting of bean
     * values. The JTextComponent.setText() method calls the the AbstractDocument.replace()
     * method. Classes that inherit AbstractDocument use use the insertString method
     * to validate the value being set.
     * 
     * The ConstrainedTextDocument.insertString() is used to validate the max length.
     * Since the insertString() method of inheriting classes typically call this class's
     * insertString() method, we need a way to bypass that behavior when programatic
     * values are being set and yet allow other text validation in inheriting classes
     * to function correctly. This behavior is governed by this flag.
     * 
     * @param bypass If true, turns on the max length validation. If false, turns it off.
     */
    public void setBypassOfMaxLengthValidation(boolean bypass)
    {
        transientBypassMaxValidation = bypass;
    }

}
