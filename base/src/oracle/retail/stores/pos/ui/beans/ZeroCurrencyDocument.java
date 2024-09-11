/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/ZeroCurrencyDocument.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:56 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.beans;

// swing imports
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;

//-------------------------------------------------------------------------
/**
    This document allows input to be valid if it meets max and min length
    requirements and is numeric.  This class also will automatically insert
    a decimal point (based on locale) to the number, which is useful for
    showing currency or significan digits. The input of zero is also accepted.
    If there is no input, then 0.00 will show(in our case).
    Used by ZeroCurrencyTextField. <p>

    @version $KW=@(#); $Ver=pos_4.5.0:2; $EKW;
    @deprecated as of release 5.5 Used by ZeroCurrencyTextField which is also deprecated.
    Now use CurrencyTextField.setZeroAllowed(boolean)
**/
//-------------------------------------------------------------------------
class ZeroCurrencyDocument extends ConstrainedTextDocument
{
    // This id is used to tell
    // the compiler not to generate a
    // new serialVersionUID.
    //
    static final long serialVersionUID = -4261590433298247424L;

    /** revision number */
    public static final String revisionNumber = "$KW=@(#); $Ver=pos_4.5.0:2; $EKW;";
    /** the default display length of the decimal */
    public static final int DEFAULT_DECIMAL_LENGTH = 2;
    /** the default pad string */
    public static final String DEFAULT_PAD = "0";
    /** the default radix point */
    public static final String DEFAULT_RADIX = ".";
    /** the length of the decimal portion of the number */
    protected int decLength;
    /** the radix indicator character */
    protected String radix;
    /** the padding string */
    protected String pad;
    /** the negative flag */
    protected boolean negative;
    /** the flag to allow negative values */
    protected boolean negativeAllowed;

    //---------------------------------------------------------------------
    /**
        Class Constructor.
    **/
    //---------------------------------------------------------------------
    public ZeroCurrencyDocument()
    {
        this(Integer.MAX_VALUE, DEFAULT_DECIMAL_LENGTH);
        this.radix = getDefaultRadix();
    }

    //---------------------------------------------------------------------
    /**
        Class Constructor. <p>
        @param maxLength the maximum length
    **/
    //---------------------------------------------------------------------
    public ZeroCurrencyDocument(int maxLength)
    {
        this(maxLength, DEFAULT_DECIMAL_LENGTH);
        this.radix = getDefaultRadix();
    }

    //---------------------------------------------------------------------
    /**
        Class Constructor. <p>
        @param maxLength the maximum length
        @param decLength the length of the decimal portion
    **/
    //---------------------------------------------------------------------
    public ZeroCurrencyDocument(int maxLength, int decLength)
    {
        super(maxLength);
        this.decLength = decLength;
        this.radix = getDefaultRadix();
        this.pad = DEFAULT_PAD;
        this.negative = false;
        this.negativeAllowed = true;
    }

    //---------------------------------------------------------------------
    /**
        Fixes up the text. <p>
        @param boolean wasNegative whether the value was negative
    **/
    //---------------------------------------------------------------------
    protected void fixupText(boolean wasNegative) throws BadLocationException
    {
        // Remove any parentheses
        if (wasNegative)
        {
            int loc = getText(0, getLength()).indexOf(getNegPrefix());
            if (loc > -1)
            {
                super.remove(loc, getNegPrefix().length());
            }
            loc = getText(0, getLength()).indexOf(getNegSuffix());
            if (loc > -1)
            {
                super.remove(loc, getNegPrefix().length());
            }
        }
        // Remove the decimal point.
        int len = getLength();
        int decPos = getText(0, getLength()).indexOf(radix);
        if (decPos > -1)
        {
            --len;
            super.remove(decPos, 1);
        }
        // Remove pad characters.
        int padLen = getPad().length();
        while (getPad().equals(getText(0, padLen)))
        {
            len -= padLen;
            super.remove(0, padLen);
        }
        // Pad the length if necessary.
        int pads = (decLength + 1) - getLength();
        //String is empty either because of no input or input of 0.
        if (pads == decLength + 1)
        {
            negative = false;
            for (int i=0; i<pads; i++)
            {
                len += padLen;
                super.insertString(0, getPad(), SimpleAttributeSet.EMPTY);
            }
            super.insertString(getLength() - decLength, radix, SimpleAttributeSet.EMPTY);
            //just end up setting text as 0.00
        }
        else
        {
            for (int i=0; i < pads ; ++i)
            {
                len += padLen;
                super.insertString(0, getPad(), SimpleAttributeSet.EMPTY);
            }
            // Add the decimal point.
            super.insertString(getLength() - decLength, radix, SimpleAttributeSet.EMPTY);
        }
        // Add the parentheses
        if (negative)   
        {       
            super.insertString(0, getNegPrefix(), SimpleAttributeSet.EMPTY);
            super.insertString(getLength(), getNegSuffix(), SimpleAttributeSet.EMPTY);
        }
    }

    //---------------------------------------------------------------------
    /**
        Returns the length of the shown decimal portion of this document.
        <p>
        @return the length of the shown decimal portion of this document
    **/
    //---------------------------------------------------------------------
    public int getDecimalLength()
    {
        return decLength;
    }

    //---------------------------------------------------------------------
    /**
        Returns the character used for a radix marker. <p>
        @return the character used for a radix marker
    **/
    //---------------------------------------------------------------------
    protected String getDefaultRadix()
    {
        // NOTE: this should come from the java.text.DecimalFormatSymbols
        return DEFAULT_RADIX;
    }

    //---------------------------------------------------------------------
    /**
        Returns the string used as the prefix to numbers
        when they are negative. <p>
        @return the negative prefix
    **/
    //---------------------------------------------------------------------
    String getNegPrefix()
    {
        return "(";
    }

    //---------------------------------------------------------------------
    /**
        Returns the string used as the suffix to numbers
        when they are negative. <p>
        @return the negative suffix
    **/
    //---------------------------------------------------------------------
    String getNegSuffix()
    {
        return ")";
    }

    //---------------------------------------------------------------------
    /**
        Returns the padding character as a string. <p>
        @return the pad character
    **/
    //---------------------------------------------------------------------
    String getPad()
    {
        return pad;
    }

    //---------------------------------------------------------------------
    /**
        Returns the radix point symbol. <p>
        @return the radix point symbol
    **/
    //---------------------------------------------------------------------
    String getRadix()
    {
        return radix;
    }

    //---------------------------------------------------------------------
    /**
        Determines if the text can be inserted.
        <b>Pre-conditions</b>
        <ul>
        <li>The string represents the text with the radix point removed.</li>
        </ul>
        <b>Post-conditions</b>
        <ul>
        <li>The text is inserted into the document if it fits and is numeric.</li>
        </ul>
        <p>
        @param offset       the offset at which the text should be inserted
        @param str          the text to be inserted
        @param attributes   the set of attributes for the text
        @exception BadLocationException if the offset is invalid
    **/
    //---------------------------------------------------------------------
    public void insertString(int offset, String str, AttributeSet attributes)
                             throws BadLocationException
    {
        //if previous value was 0.00, then don't append to it, just get rid of it.
        String defaultValue = pad+radix.charAt(0);
        for (int i=0; i < decLength; i++)
        {
            defaultValue+=pad;
        }
        if (getText(0, getLength()).equals(defaultValue))
        {
            super.remove(0, getLength());
            offset = 0;
        }
      
        // If it's a number, let it go in.  Then fix the number of digits
        // and the decimal point.
        StringBuffer modStr = new StringBuffer("");
        int len = str.length();
        boolean numeric = true;
        boolean wasNegative = negative;
        for (int i=0; i < len ; ++i)
        {
            char currChar = str.charAt(i);
            if (currChar == '-')
            {
                if (negativeAllowed)
                {
                    negative = !negative;
                }
                else
                {
                    numeric = false;
                }
            }
            else if (Character.isDigit(currChar))
            {
                // Build a numeric-only string.
                modStr.append(currChar);
            }
            else if (currChar == radix.charAt(0))
            {
                //see if there's all ready a Decimal point
                //**********************************************// 
                /*
                    NOTE IMPLICIT BEHAVOIR THAT YOU CAN ONLY ENTER
                    ONE DECIMAL PLACE NEED TO BE ABLE TO ENTER DEC
                    FROM KEY BOARD INPUT ALSO
                */
                //**********************************************// 
                int decPos = getText(0, getLength()).indexOf(radix);

                if (decPos != -1) //number all ready has Decimal point
                {
                    numeric = false;
                }
                else //need to see if Decimal place is cool
                {
                    modStr.append(currChar);                
                }
            }
            else
            {
                numeric = false;
            }
        }
        if (numeric)
        {
            super.insertString(offset, modStr.toString(), attributes);
            fixupText(wasNegative);
        }
    }

    //---------------------------------------------------------------------
    /**
        Returns whether negative values are allowed in this field. <p>
        @return true if negative numbers allowed, false otherwise
    **/
    //---------------------------------------------------------------------
    public boolean isNegativeAllowed()
    {
        return negativeAllowed;
    }

    //---------------------------------------------------------------------
    /**
        Removes text from this model.
        <p>
        @param offset the offset at which text will be removed
        @param length the number of characters to be removed
    **/
    //---------------------------------------------------------------------
    public void remove(int offset, int length) throws BadLocationException
    {
        // Remove the extra crud and find out which digit should be deleted.
        if (negative)
        {
            // Remove the suffix.
            super.remove(getLength() - getNegSuffix().length(), getNegSuffix().length());
            if (offset >= getLength())
            {
                offset -= getNegSuffix().length();
            }
            if (length > 1 && offset + length > getLength())
            {
                length -= getNegSuffix().length();
            }
            // Adjust the length.
            if (offset + length > getLength())
            {
                length = getLength() - offset;
            }
            // Remove the prefix.
            super.remove(0, getNegPrefix().length());
            if (offset < getNegPrefix().length())
            {
                // Adjust the length.
                length -= getNegPrefix().length();
            }
            else
            {
                offset -= getNegPrefix().length();
            }
        }
        int loc = getText(0, getLength()).indexOf(radix);
        if (loc > -1)
        {
            super.remove(loc, radix.length());
            if (offset <= loc && offset + length >= loc + radix.length())
            {
                length -= radix.length();
            }
            else if (offset > loc + radix.length())
            {
                offset -= radix.length();
            }
            else
            {
                // Want to delete the radix?
                offset = loc;
            }
        }
        // Delete the digit(s).
        super.remove(offset, length);
        // Fix up the output.
        fixupText(negative);
    }

    //---------------------------------------------------------------------
    /**
        Sets the length of the shown decimal portion of the number.
        <p>
        @param decLength    the number of digits after the decimal point
    **/
    //---------------------------------------------------------------------
    public void setDecimalLength(int decLength)
    {
        this.decLength = decLength;
    }

    //---------------------------------------------------------------------
    /**
        Sets the flag to allow or disallow negative numbers. <p>
        @param negativeAllowed true if negative numbers should be allowed,
                               false otherwise
    **/
    //---------------------------------------------------------------------
    public void setNegativeAllowed(boolean negativeAllowed)
    {
        this.negativeAllowed = negativeAllowed;
    }
}
