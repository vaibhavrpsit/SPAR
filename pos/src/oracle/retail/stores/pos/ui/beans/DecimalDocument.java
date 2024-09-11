/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 *  Copyright (c) 2016 - 2017 MAX Hypermarket, Inc.    All Rights Reserved.
 *	
 *  Rev 1.0     Mar 03, 2017        Nitika Arora        PLU item qty should not be changed at the time of return(decimal precsion shoud be 3digits)(Make the class as public)
 *  
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
package oracle.retail.stores.pos.ui.beans;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;

import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.utility.LocaleConstantsIfc;
import oracle.retail.stores.common.utility.LocaleMap;


//-------------------------------------------------------------------------
/**
   This document allows input to be valid if it meets max and min length
   requirements and is numeric.  This class also will automatically insert
   a decimal point (based on locale) to the number, which is useful for
   showing currency or significan digits.

   @version $KW=@(#); $Ver=pos_4.5.0:75; $EKW;
*/
//-------------------------------------------------------------------------
public class DecimalDocument extends ConstrainedTextDocument
{
    // This id is used to tell
    // the compiler not to generate a
    // new serialVersionUID.
    //
    static final long serialVersionUID = 347730721015583402L;

    /** revision number supplied by Team Connection */
    public static final String revisionNumber = "$Revision: /main/18 $";
    /** the default display length of the decimal */
    public static final int DEFAULT_DECIMAL_LENGTH = 2;
    /** the default pad string */
    public static final String DEFAULT_PAD = "0";

    /** the default radix point
        @deprecated as of release 5.5 use getDecimalSeparator() for the locale from the DecimalFormatSymbols class
    **/
    public static final String DEFAULT_RADIX = ".";

    /** the length of the decimal portion of the number */
    protected int decLength;

    /** the radix indicator character */
    protected String radix = DEFAULT_RADIX;

    /** the padding string */
    protected String pad;

    /** the negative flag */
    protected boolean negative;

    /** the flag to allow negative values */
    protected boolean negativeAllowed;

    /** the flag to allow cero values */
    protected boolean zeroAllowed;

    /** the maximum length */
    protected int maximumLength;

    protected DecimalFormat format =null;
    protected DecimalFormatSymbols symbols = null;
    protected Locale locale=null;

    //---------------------------------------------------------------------
    /**
       Constructor.
    */
    //---------------------------------------------------------------------
    public DecimalDocument()
    {
        this(Integer.MAX_VALUE, DEFAULT_DECIMAL_LENGTH);
    }
    //---------------------------------------------------------------------
    /**
       Constructor.
       @param maxLength the maximum length
    */
    //---------------------------------------------------------------------
    public DecimalDocument(int maxLength)
    {
        this(maxLength, DEFAULT_DECIMAL_LENGTH);
    }
    //---------------------------------------------------------------------
    /**
       Constructor.
       @param maxLength the maximum length
       @param decLength the length of the decimal portion
    */
    //---------------------------------------------------------------------
    public DecimalDocument(int maxLen, int decLen)
    {
        super(maxLen);
        setupFormat();
        decLength = decLen;
        pad = DEFAULT_PAD;
        negative = false;
        negativeAllowed = true;
        zeroAllowed = true;


    }



    //---------------------------------------------------------------------
     /**
        Gets the decimal format use in this document.
        @return the decimal format use in this document
     */
     //---------------------------------------------------------------------
     public DecimalFormat getFormat()
     {
         return format;
     }
    //---------------------------------------------------------------------
    /**
       Gets the length of the shown decimal portion of this document.
       @return the length of the shown decimal portion of this document
    */
    //---------------------------------------------------------------------
    public int getDecimalLength()
    {
        return decLength;
    }
    //---------------------------------------------------------------------
    /**
       Returns the character used for a radix marker.
       @return the character used for a radix marker
    */
    //---------------------------------------------------------------------
    protected char getDefaultRadix()
    {
        return symbols.getDecimalSeparator();
    }

    //---------------------------------------------------------------------
    /**
       Returns the character used for a radix marker.
       @return the character used for a radix marker
    */
    //---------------------------------------------------------------------
    protected char getRadix()
    {
        return radix.charAt(0);
    }
    //---------------------------------------------------------------------
    /**
       Returns the string used as the prefix to numbers when they are negative.
       @return the string used as the prefix to numbers when they are negative
    */
    //---------------------------------------------------------------------
    public String getNegPrefix()
    {
       return format.getNegativePrefix();
    }

    //---------------------------------------------------------------------
    /**
       Returns the string used as the suffix to numbers when they are negative.
       @return the string used as the suffix to numbers when they are negative
    */
    //---------------------------------------------------------------------
    public String getNegSuffix()
    {
       return format.getNegativeSuffix();
    }
    //---------------------------------------------------------------------
    /**
       Gets the padding character as a string.
    */
    //---------------------------------------------------------------------
    public  String getPad()
    {
        return pad;
    }

    //---------------------------------------------------------------------
    /**
       Returns whether negative values are allowed in this field.
       @return true if negative numbers allowed, false otherwise
    */
    //---------------------------------------------------------------------
    public boolean isNegativeAllowed()
    {
        return negativeAllowed;
    }


    //---------------------------------------------------------------------
    /**
       Sets the flag to allow or disallow negative numbers.
       @param negativeAllowed true if negative numbers should be allowed,
       false otherwise
    */
    //---------------------------------------------------------------------
    public void setNegativeAllowed(boolean value)
    {
        negativeAllowed = value;
    }
    //---------------------------------------------------------------------
    /**
       Sets the length of the shown decimal portion of the number.
    */
    //---------------------------------------------------------------------
    public void setDecimalLength(int value)
    {
       decLength = value;
    }


    //---------------------------------------------------------------------
     /**
        Sets up the decimal format.
     */
     //---------------------------------------------------------------------
    protected void setupFormat()
    {
        locale = LocaleMap.getLocale(LocaleConstantsIfc.USER_INTERFACE);
        format = DomainGateway.getFormat(locale,
                                         LocaleConstantsIfc.DECIMAL_FORMAT_PROPERTY,
                                         LocaleConstantsIfc.DEFAULT_DECIMAL_FORMAT);

        symbols = format.getDecimalFormatSymbols();
        //default maxLength
    //    maxLength = format.getMaximumIntegerDigits();
        setMaxLength(format.getMaximumIntegerDigits());

         // Obtain fraction piece from the getCurrency.getfraction
        decLength = format.getMaximumFractionDigits();
        radix = Character.toString(getDefaultRadix());
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
       @param offset the offset at which the text should be inserted
       @param str the text to be inserted
       @param attributes the set of attributes for the text
       @exception BadLocationException if the offset is invalid
    */
    //---------------------------------------------------------------------
    public void insertString(int offset, String str, AttributeSet attributes)
        throws BadLocationException
    {
        //strip neg if needed and adjust offset
        if (negative)
        {
            if (offset >= getLength())
            {
                offset -= (getNegSuffix().length() + getNegPrefix().length());
            }

            else if (offset > getNegPrefix().length())
            {
                offset -= getNegPrefix().length();
            }
            else
            {
                offset = 0;
            }
            negate(false);
        }

        boolean numeric = true;
        StringBuffer modStr = new StringBuffer();
        int len = str.length();
        int decPos = getText(0, getLength()).indexOf(radix);
        boolean decSet =  (decPos > -1);
        //remember we add 1 to max len if Dec
        int intLen = (decSet) ? getMaxLength() - decLength - 1
            : getMaxLength() - decLength;
        int textLen =  getLength();

        for (int i=0; i < len && numeric ; ++i)
        {
            char currChar = str.charAt(i);
            int strLen = modStr.length();
            int decPosModStr = modStr.toString().indexOf(radix);
            boolean isDecInModStr = (decPosModStr != -1);

            if (Character.isDigit(currChar))
            {
                // if theres no Radix
                if (!decSet && (strLen + textLen) >= intLen)
                {
                    numeric = false;
                }
                // if theres a Radix and where inserting after it
                // and Radix on in modStr
                else if(decSet && decPos < offset && !isDecInModStr &&
                    ((textLen + strLen)  - (decPos + 1)) >= decLength)
                {
                    numeric = false;
                }
                // if theres a Radix and where inserting before it
                // and Radix on in modStr
                else if (decSet && decPos >= offset && !isDecInModStr &&
                    (decPos + strLen) >= intLen)
                {
                    numeric = false;
                }
                // if Radix in Modstr you got to figure
                else if (isDecInModStr &&
                    (textLen - offset + strLen - (decPosModStr + 1)) >= decLength)
                {
                    //you incremented when you inserted it so
                    setMaxLength(getMaxLength() - 1);
                    numeric = false;
                }
                else
                {
                    modStr.append(currChar);
                }
            }
            else if (currChar == symbols.getMinusSign())
            {
                /***************************************************
                only allow negation if it's allowed and if it's just
                the - char and the current text has 1 or more digits
                or if the modifying string has more than 1 digit the
                - char is the first and the insertion is happening
                at the begining of the text
                ***************************************************/
                if ( negativeAllowed && ((getLength() >= (allowDanglingNegativeSign()? 0: 1) && len == 1) ||
                        (i == 0 && offset == 0 && len > 1)))
                {
                    negative = !negative;
                }
                else
                {
                    numeric = false;
                }

            }
            else if (currChar == radix.charAt(0))
            {
                /***************************************************
                only allow the Radix if it will not exceed the max
                Decimals or if it's on all ready in text
                ***************************************************/
                if (decSet || (getLength() - offset) > decLength ||
                    (offset + i) > intLen)
                {
                     numeric = false;
                }
                else
                {
                    decSet = true;
                    decPos = offset + i;
                    super.setMaxLength(getMaxLength() + 1);
                    modStr.append(currChar);
                }
            }
        }
        if (numeric)
        {
            super.insertString(offset, modStr.toString(), attributes);
        }
        if (negative)
        {
            negate(true);
        }
    }

    //---------------------------------------------------------------------
    /**
       Removes text from this model.
       @param offset the offset at which text will be removed
       @param length the number of characters to be removed
    */
    //---------------------------------------------------------------------
    public void remove(int offset, int length) throws BadLocationException
    {
        //strip neg sign if applicable
        if (negative)
        {
            if(offset == 0 && length >0 && allowDeleteNegativeSign())
            {
                negative = false;
            }
            super.remove(getLength() - getNegSuffix().length(),
                         getNegSuffix().length());
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

            setMaxLength(getMaxLength() - 2);
        }

        // if we're stripping the radix dec maxlen and clear it!!!
        if (getText(offset, length).indexOf(radix) != -1)
        {
            super.setMaxLength(maximumLength);
            super.remove(0, getLength());
        }
        else
        {
            super.remove(offset, length);
        }
        if(getLength() == 0 && !allowDanglingNegativeSign())
        {
            negative = false;
        }

        if(negative)
        {
          negate(true);
        }
    }




    //---------------------------------------------------------------------
    /**
       Sets the maximum length of a valid document.
       @param maxLength the maximum length for a valid document
    */
    //---------------------------------------------------------------------
    public void setMaxLength(int maxLength)
    {
        super.setMaxLength(maxLength);
        maximumLength = maxLength;
    }
    //---------------------------------------------------------------------
     /**
        Sets the flag to allow or disallow a zero value.
        @param zeroAllowed true if a zero value should be allowed,
        false otherwise
     */
     //---------------------------------------------------------------------
     public void setZeroAllowed(boolean value)
     {
        zeroAllowed = value;
     }
     //---------------------------------------------------------------------
     /**
        Returns whether a zero value is allowed in this field.
        @return true if zero value is allowed, false otherwise
     */
     //---------------------------------------------------------------------
     public boolean isZeroAllowed()
     {
         return zeroAllowed;
     }
    //---------------------------------------------------------------------
    /**
       Negate the current text.
       @param isNeg true if to set the number to negative
    */
    //---------------------------------------------------------------------
    protected void negate(boolean isNeg) throws  BadLocationException
    {
      if (isNeg)
      {
          setMaxLength(getMaxLength() + 2);
          if (getNegPrefix() != null)
          {
              super.insertString(0, getNegPrefix(), SimpleAttributeSet.EMPTY);
          }
          if (getNegSuffix() != null)
          {
              super.insertString(getLength(), getNegSuffix(),SimpleAttributeSet.EMPTY);
          }
      }
      else
      {
          setMaxLength(getMaxLength() - 2);
          if (getNegPrefix() != null)
          {
              super.remove(0, getNegPrefix().length());
          }
          if (getNegSuffix() != null)
          {
              super.remove(getLength() - getNegSuffix().length(), getNegSuffix().length());
          }

      }
    }
    //---------------------------------------------------------------------

    /**
     Determine if the dangling negative sign is allowed
     */
    //---------------------------------------------------------------------
    protected boolean allowDanglingNegativeSign()
    {
        return false;
    }

    //---------------------------------------------------------------------
    /**
     Determine if user is allowed to delete negative sign
     */
    //---------------------------------------------------------------------
    protected boolean allowDeleteNegativeSign()
    {
        return false;
    }

}
