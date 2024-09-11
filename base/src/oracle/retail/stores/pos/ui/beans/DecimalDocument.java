/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/DecimalDocument.java /main/18 2011/12/05 12:16:23 cgreene Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   12/05/11 - updated from deprecated packages and used more
 *                         bigdecimal constants
 *    cgreene   05/28/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/26/10 - XbranchMerge cgreene_tech43 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    cgreene   04/02/10 - remove deprecated LocaleContantsIfc and currencies
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *   5    360Commerce 1.4         4/30/2008 12:14:36 PM  Kun Lu          Fix CR
 *         31129. Hard code integer format and allow user to input integer in
 *        a free style
 *   4    360Commerce 1.3         4/25/2008 9:20:08 PM   Kun Lu          Fix CR
 *         31129
 *   3    360Commerce 1.2         3/31/2005 4:27:42 PM   Robert Pearse   
 *   2    360Commerce 1.1         3/10/2005 10:20:51 AM  Robert Pearse   
 *   1    360Commerce 1.0         2/11/2005 12:10:31 PM  Robert Pearse   
 *
 *  Revision 1.5  2004/09/23 00:07:11  kmcbride
 *  @scr 7211: Adding static serialVersionUIDs to all POS Serializable objects, minus the JComponents
 *
 *  Revision 1.4  2004/03/16 17:15:22  build
 *  Forcing head revision
 *
 *  Revision 1.3  2004/03/16 17:15:17  build
 *  Forcing head revision
 *
 *  Revision 1.2  2004/02/11 20:56:26  rhafernik
 *  @scr 0 Log4J conversion and code cleanup
 *
 *  Revision 1.1.1.1  2004/02/11 01:04:21  cschellenger
 *  updating to pvcs 360store-current
 *
 *
 *
 *    Rev 1.2   Dec 01 2003 14:33:32   baa
 * fix formating of decimal positions
 * Resolution for 3486: Decimal point appears on Trans Tax Override, req state must enter decimal point
 *
 *    Rev 1.0.1.0   Oct 28 2003 13:43:16   baa
 * refactor  decimal formatting
 *
 *    Rev 1.0   Aug 29 2003 16:10:06   CSchellenger
 * Initial revision.
 *
 *    Rev 1.4   Mar 07 2003 17:11:08   baa
 * code review changes for I18n
 * Resolution for POS SCR-1740: Code base Conversions
 *
 *    Rev 1.3   Sep 23 2002 13:26:44   baa
 * fix decimal field
 * Resolution for POS SCR-1740: Code base Conversions
 *
 *    Rev 1.2   Sep 20 2002 18:03:06   baa
 * country/state fixes and other I18n changes
 * Resolution for POS SCR-1740: Code base Conversions
 *
 * ===========================================================================
 */
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
class DecimalDocument extends ConstrainedTextDocument
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
