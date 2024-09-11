/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/CurrencyDocument.java /main/19 2011/12/05 12:16:23 cgreene Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   12/05/11 - updated from deprecated packages and used more
 *                         bigdecimal constants
 *    cgreene   06/17/11 - change grouping and decimal separator to chars
 *    cgreene   03/18/11 - XbranchMerge cgreene_124_receipt_quick_wins from
 *                         main
 *    cgreene   03/16/11 - updated to adjust decimal point if deleting zeroes
 *                         from end.
 *    acadar    06/10/10 - use default locale for currency display
 *    acadar    06/09/10 - XbranchMerge acadar_tech30 from
 *                         st_rgbustores_techissueseatel_generic_branch
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
 *    9    360Commerce 1.8         2/4/2008 12:09:49 PM   Charles D. Baker CR
 *         29652 - Repaired ConstrainedTextField to permit insertString
 *         override of insertString in implementing classes. Code reviewed by
 *         Sandy Gu.
 *    8    360Commerce 1.7         8/21/2007 10:09:45 AM  Anda D. Cadar   CR
 *         28399: Fix for display of negative amounts
 *    7    360Commerce 1.6         8/2/2007 8:59:28 PM    Ashok.Mondal    CR
 *         28042 :Display correct formatted check amount on check referral
 *         screen.
 *    6    360Commerce 1.5         7/3/2007 9:50:34 AM    Anda D. Cadar   get
 *         the number of decimal digits from base currency type
 *    5    360Commerce 1.4         6/19/2007 1:58:10 PM   Anda D. Cadar
 *         additional cleanup for I18N
 *    4    360Commerce 1.3         5/18/2007 9:18:14 AM   Anda D. Cadar   EJ
 *         and currency UI changes
 *    3    360Commerce 1.2         3/31/2005 4:27:34 PM   Robert Pearse
 *    2    360Commerce 1.1         3/10/2005 10:20:30 AM  Robert Pearse
 *    1    360Commerce 1.0         2/11/2005 12:10:16 PM  Robert Pearse
 *
 *   Revision 1.6  2004/09/23 00:07:11  kmcbride
 *   @scr 7211: Adding static serialVersionUIDs to all POS Serializable objects, minus the JComponents
 *
 *   Revision 1.5  2004/04/08 20:33:02  cdb
 *   @scr 4206 Cleaned up class headers for logs and revisions.
 *
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

import oracle.retail.stores.commerceservices.common.currency.CurrencyServiceIfc;
import oracle.retail.stores.commerceservices.common.currency.CurrencyServiceLocator;
import oracle.retail.stores.common.utility.LocaleMap;

/**
 * This document allows input to be valid if it meets max and min length
 * requirements and is numeric. This class also will automatically insert a
 * decimal point (based on locale) to the number, which is useful for showing
 * currency or significant digits.
 * 
 * @version $Revision: /main/19 $;
 */
class CurrencyDocument extends ConstrainedTextDocument
{
    // This id is used to tell the compiler not to generate a new serialVersionUID.
    static final long serialVersionUID = -7889514667626707155L;

    /** revision number supplied by Team Connection */
    public static final String revisionNumber = "$Revision: /main/19 $";

    /** the default display length of the decimal */
    public static final int DEFAULT_DECIMAL_LENGTH = 2;

    /** the default pad string */
    public static final String DEFAULT_PAD = "0";

    /** the length of the decimal portion of the number */
    protected int decLength;

    /** the radix indicator character */
    protected char decimalPoint = '.';

    /** Grouping separator */
    protected char groupingSeparator= ',';

    /** the padding string */
    protected String pad;

    /** the negative flag */
    protected boolean negative;

    /** the flag to allow negative values */
    protected boolean negativeAllowed;

    /** the flag to allow zero values */
    protected boolean zeroAllowed;

    /** the maximum length */
    protected int maximumLength;

    protected DecimalFormat format = null;

    protected DecimalFormatSymbols symbols = null;

    /**
     * User Locale
     */
    protected Locale locale = null;

    /**
     * CurrencyService
     */
    protected static CurrencyServiceIfc currencyService;

    /** Check if editable field */
    protected boolean isEditable = true;

    /**
     * Negative prefix
     */
    protected String negativePrefix;

    /**
     * Negative suffix
     */
    protected String negativeSuffix;

    /**
     * Gets an instance of CurrencyService
     * 
     * @return
     */
    public static CurrencyServiceIfc getCurrencyService()
    {
        if (currencyService == null)
        {
            currencyService = CurrencyServiceLocator.getCurrencyService();
        }
        return currencyService;
    }

    /**
     * Constructor.
     */
    public CurrencyDocument()
    {
        this(Integer.MAX_VALUE, DEFAULT_DECIMAL_LENGTH);
    }

    /**
     * Constructor.
     * 
     * @param maxLength the maximum length
     */
    public CurrencyDocument(int maxLength)
    {
        this(maxLength, DEFAULT_DECIMAL_LENGTH);
    }

    /**
     * Constructor.
     * 
     * @param maxLength the maximum length
     * @param decLength the length of the decimal portion
     */
    public CurrencyDocument(int maxLength, int decLen)
    {
        super(maxLength);
        setupDecimalFormat();
        decLength = decLen;
        pad = DEFAULT_PAD;
        negative = false;
        negativeAllowed = true;
        zeroAllowed = true;
    }

    /**
     * Returns the string used as the prefix to numbers when they are negative.
     * 
     * @return the string used as the prefix to numbers when they are negative
     */
    public String getNegPrefix()
    {
        if (negativePrefix == null)
        {
            negativePrefix = removeCurrencySymbol(format.getNegativePrefix(),
                    format.getDecimalFormatSymbols().getCurrencySymbol());
        }
        return negativePrefix;
    }

    /**
     * Returns the string used as the suffix to numbers when they are negative.
     * 
     * @return the string used as the suffix to numbers when they are negative
     */
    public String getNegSuffix()
    {
        if (negativeSuffix == null)
        {
            negativeSuffix = removeCurrencySymbol(format.getNegativeSuffix(),
                    format.getDecimalFormatSymbols().getCurrencySymbol());
        }
        return negativeSuffix;
    }

    /**
     * Gets the padding character as a string.
     */
    public String getPad()
    {
        return pad;
    }

    /**
     * Returns the character used for a radix marker.
     * 
     * @return the character used for a radix marker
     */
    protected char getDefaultRadix()
    {
        return symbols.getMonetaryDecimalSeparator();
    }

    /**
     * Sets the maximum length of a valid document.
     * 
     * @param maxLength the maximum length for a valid document
     */
    public void setMaxLength(int maxLength)
    {
        super.setMaxLength(maxLength);
        maximumLength = maxLength;
    }

    /**
     * Sets up the decimal format.
     */
    protected void setupDecimalFormat()
    {
        locale = LocaleMap.getLocale(LocaleMap.DEFAULT);
        format = getCurrencyService().getCurrencyFormat(locale);
        symbols = format.getDecimalFormatSymbols();
        maxLength = format.getMaximumIntegerDigits(); // default maxLength
        decLength = format.getMaximumFractionDigits();
        decimalPoint = format.getDecimalFormatSymbols().getDecimalSeparator();
        groupingSeparator = format.getDecimalFormatSymbols().getGroupingSeparator();
    }

    /**
     * Gets the decimal format use in this document.
     * 
     * @return the decimal format use in this document
     */
    public DecimalFormat getFormat()
    {
        return format;
    }

    /**
     * Determines if the text can be inserted.
     * <p>
     * <b>Pre-conditions</b>
     * <ul>
     * <li>The string represents the text with the radix point removed.</li>
     * </ul>
     * <b>Post-conditions</b>
     * <ul>
     * <li>The text is inserted into the document if it fits and is numeric.</li>
     * </ul>
     * 
     * @param offset the offset at which the text should be inserted
     * @param str the text to be inserted
     * @param attributes the set of attributes for the text
     * @exception BadLocationException if the offset is invalid
     */
    @Override
    public void insertString(int offset, String str, AttributeSet attributes) throws BadLocationException
    {
        // If it's a number, let it go in. Then fix the number of digits and the decimal point.
        int len = str.length();
        StringBuilder modStr = new StringBuilder(len);
        boolean numeric = true;
        boolean wasNegative = negative;
        for (int i = 0; i < len; ++i)
        {
            char currChar = str.charAt(i);
            if (currChar == symbols.getMinusSign())
            {
                /***************************************************
                 * only allow negation if it's allowed and if it's just the
                 * minus sign or negative prefix and the current text has 1 or
                 * more digits or if the modifying string has more than 1 digit
                 * the minus sign is the first and the insertion is happening at
                 * the beginning of the text or at the end of the text
                 ***************************************************/
                if (negativeAllowed && ((getLength() > 0 && len == 1) || (i == 0 && offset == 0 && len > 1)))

                {
                    negative = !negative;
                }
                else
                {
                    numeric = false;
                    break;
                }

            }
            else if (Character.isDigit(currChar) || (!isEditable() && currChar == groupingSeparator))
            {
                // Build a numeric-only string.
                modStr.append(currChar);
            } // ignore radix if it is the first character

            else if (currChar == decimalPoint && i > 0)
            {
                // see if there's all ready a Decimal point
                int decPos = getText(0, getLength()).indexOf(decimalPoint);

                if (decPos != -1) // number all ready has Decimal point
                {
                    numeric = false; // can't have two decimals.
                    break;
                }

                modStr.append(currChar);
            }
            else
            {
                numeric = false;
                break;
            }
        }

        if (numeric)
        {
            super.insertString(offset, modStr.toString(), attributes);
            fixupText(wasNegative);
        }
    }

    /**
     * Sets the length of the shown decimal portion of the number.
     */
    public void setDecimalLength(int value)
    {
        decLength = value;
    }

    /**
     * Adds or Remove negative prefixes/suffixes. Inserts the decimal point
     * at the correct location.
     * 
     * @param boolean wasNegative whether the value was negative
     */
    protected void fixupText(boolean wasNegative) throws BadLocationException
    {
        if (wasNegative)
        {
            int loc = -1;
            if (getNegPrefix() != null)
            {
                int prefixLen = getNegPrefix().length();
                loc = getText(0, getLength()).indexOf(getNegPrefix());
                if (loc > -1)
                {
                    super.remove(loc, prefixLen);
                }
            }
            if (getNegSuffix() != null)
            {
                int suffixLen = getNegSuffix().length();
                loc = getText(0, getLength()).indexOf(getNegSuffix());
                if (loc > -1)
                {
                    super.remove(loc, suffixLen);
                }
            }
        }
        // Remove the decimal point.
        int len = getLength();
        int decPos = getText(0, getLength()).indexOf(decimalPoint);
        if (decPos > -1)
        {
            --len;
            super.remove(decPos, 1);
        }
        // Remove pad characters.
        int padLen = getPad().length();

        if (zeroAllowed)
        {
            int firstNonZeroIndex = 0;
            char c = getText(firstNonZeroIndex, 1).charAt(0);

            while (c == symbols.getZeroDigit() || c == symbols.getMonetaryDecimalSeparator())
            {
                c = getText(firstNonZeroIndex, 1).charAt(0);
                firstNonZeroIndex++;
            }

            if (0 < firstNonZeroIndex && firstNonZeroIndex < getLength())
            {
                while (getPad().equals(getText(0, padLen)))
                {
                    len -= padLen;
                    super.remove(0, padLen);
                }
            }
        }
        else
        {
            while (getPad().equals(getText(0, padLen)))
            {
                len -= padLen;
                super.remove(0, padLen);
            }
        }

        // Pad the length if necessary.
        int pads = (decLength + 1) - getLength();
        if (pads == decLength + 1)
        {
            negative = false;
        }
        else
        {
            for (int i = 0; i < pads; ++i)
            {
                len += padLen;
                super.insertString(0, getPad(), SimpleAttributeSet.EMPTY);
            }
            // Convert char to string
            char[] decSymbol = new char[1];
            decSymbol[0] = decimalPoint;
            String decimalSymbol = new String(decSymbol);

            // Add the decimal point.
            super.insertString(getLength() - decLength, decimalSymbol, SimpleAttributeSet.EMPTY);
        }
        // Add the negative symbol
        if (negative)
        {
            if (getNegPrefix() != null)
            {
                super.insertString(0, getNegPrefix(), SimpleAttributeSet.EMPTY);
            }
            if (getNegSuffix() != null)
            {
                super.insertString(getLength(), getNegSuffix(), SimpleAttributeSet.EMPTY);
            }
        }

    }

    /**
     * Remove Currency Symbol from the prefix or suffix
     * 
     * @param value
     * @param symbol
     * @return prefix/suffix without currency symbol
     */
    protected String removeCurrencySymbol(String value, String symbol)
    {
        String newValue = "";
        int symbolLength = symbol.length();
        if (value.indexOf(symbol) != -1)
        {
            if (value.startsWith(symbol))
            {
                value = value.substring(symbolLength);
            }
            else if (value.endsWith(symbol))
            {
                value = value.substring(0, value.length() - symbolLength);
            }
        }
        newValue = value;
        return newValue;
    }

    /**
     * Removes text from this model.
     * 
     * @param offset the offset at which text will be removed
     * @param length the number of characters to be removed
     */
    @Override
    public void remove(int offset, int length) throws BadLocationException
    {
        // strip neg sign if applicable
        boolean charsStripped = false;
        if (negative)
        {
            // remove the suffix
            int negSuffixLen = getNegSuffix().length();
            super.remove(getLength() - negSuffixLen, negSuffixLen);
            if (offset >= getLength())
            {
                offset -= negSuffixLen;
            }
            if (length > 1 && offset + length > getLength())
            {
                length -= negSuffixLen;
            }

            // adjust the length.
            if (offset + length > getLength())
            {
                length = getLength() - offset;
            }

            // remove the prefix.
            int negPrefixLen = getNegPrefix().length();
            super.remove(0, negPrefixLen);
            if (offset < negPrefixLen)
            {
                // adjust the length.
                length -= negPrefixLen;
            }
            else
            {
                offset -= negPrefixLen;
            }
            setMaxLength(getMaxLength() - 2);
            charsStripped = true;
        }

        // if we're stripping the radix, dec maxlen and clear all text
        if (getText(offset, length).indexOf(decimalPoint) != -1)
        {
            super.setMaxLength(maximumLength);
            super.remove(0, getLength());
        }
        else
        {
            super.remove(offset, length);
            // if a decimal value was removed, move the radix to correct spot
            String currentText = getText(0, getLength());
            int radixIndex = currentText.indexOf(decimalPoint); 
            if (radixIndex > 0)
            {
                int expectedOffset = currentText.length() - decLength - 1;
                if (radixIndex != expectedOffset)
                {
                    super.remove(radixIndex, 1);
                    insertStringWithoutCharValidation(expectedOffset, String.valueOf(decimalPoint), null);
                }
            }
        }

        if (getLength() == 0)
        {
            negative = false;
            if (charsStripped)
            { // revert to the maximum length
                maximumLength = maximumLength + 2;
            }
            super.setMaxLength(maximumLength);
        }

        if (negative)
        {
            negate(true);
        }
    }

    /**
     * Negate the current text.
     * 
     * @param isNeg true if to set the number to negative
     */
    protected void negate(boolean isNeg) throws BadLocationException
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
                super.insertString(getLength(), getNegSuffix(), SimpleAttributeSet.EMPTY);
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

    /**
     * Returns whether negative values are allowed in this field.
     * 
     * @return true if negative numbers allowed, false otherwise
     */
    public boolean isNegativeAllowed()
    {
        return negativeAllowed;
    }

    /**
     * Sets the flag to allow or disallow negative numbers.
     * 
     * @param negativeAllowed true if negative numbers should be allowed, false
     *            otherwise
     */
    public void setNegativeAllowed(boolean value)
    {
        negativeAllowed = value;
    }

    /**
     * Sets the flag to allow or disallow a zero value.
     * 
     * @param zeroAllowed true if a zero value should be allowed, false
     *            otherwise
     */
    public void setZeroAllowed(boolean value)
    {
        zeroAllowed = value;
    }

    /**
     * Returns whether a zero value is allowed in this field.
     * 
     * @return true if zero value is allowed, false otherwise
     */
    public boolean isZeroAllowed()
    {
        return zeroAllowed;
    }

    /**
     * Sets the editable flag for the field.
     * 
     * @param value true if field is editable, false otherwise
     */
    public void setEditable(boolean value)
    {
        isEditable = value;
    }

    /**
     * Returns whether this field is editable.
     * 
     * @return true if editable, false otherwise
     */
    public boolean isEditable()
    {
        return isEditable;
    }
}
