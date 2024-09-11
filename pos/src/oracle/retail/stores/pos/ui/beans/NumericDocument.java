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
import javax.swing.text.GapContent;

import oracle.retail.stores.domain.utility.LocaleConstantsIfc;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.common.utility.LocaleMap;

/**
 * This document allows input to be valid if it meets max and min length
 * requirements and is numeric.
 * 
 * @version $KW=@(#); $Ver=pos_4.5.0:2; $EKW;
 */
public class NumericDocument extends ConstrainedTextDocument
{
    // This id is used to tell the compiler not to generate a new serialVersionUID.
    static final long serialVersionUID = -5976187536032664534L;

    /** revision number supplied by Team Connection */
    public static final String revisionNumber = "$Revision: /main/14 $";

    /** the flag to allow zero values */
    protected boolean zeroAllowed;

    protected String zeroDigit;

    /**
     * Constructor.
     */
    public NumericDocument()
    {
        this(Integer.MAX_VALUE);
    }

    /**
     * Constructor.
     * 
     * @param maxLength the maximum length
     */
    public NumericDocument(int maxLength)
    {
        super(maxLength, new GapContent());

        /*
         * The zeroDigit has to be initialized. Not initializing it will allow
         * entry of zero even when 'zeroAllowed' is false.
         */
        Locale locale = LocaleMap.getLocale(LocaleConstantsIfc.USER_INTERFACE);
        DecimalFormat format = DomainGateway.getDecimalFormat(locale);
        DecimalFormatSymbols symbols = format.getDecimalFormatSymbols();
        zeroDigit = new Character(symbols.getZeroDigit()).toString();
    }

    /**
     * Constructor.
     * 
     * @param maxLength the maximum length
     */
    public NumericDocument(int maxLength, Content content)
    {
        super(maxLength, content);
        Locale locale = LocaleMap.getLocale(LocaleConstantsIfc.USER_INTERFACE);
        DecimalFormat format = DomainGateway.getDecimalFormat(locale);
        DecimalFormatSymbols symbols = format.getDecimalFormatSymbols();
        zeroDigit = new Character(symbols.getZeroDigit()).toString();
    }

    /**
     * Determines if the text can be inserted. Overridden to call
     * 
     * @param offset the offset at which the text should be inserted
     * @param text the text to be inserted
     * @param attributes the set of attributes for the text
     * @exception BadLocationException if the offset is invalid
     */
    @Override
    public void insertString(int offset, String text, AttributeSet attributes) throws BadLocationException
    {
        if (text != null && isNumeric(text))
        {
            if (getLength() > 0)
            {
                super.insertString(offset, text, attributes);
            }
            else if (zeroAllowed || (!zeroAllowed && !text.equals(zeroDigit)))
            {
                super.insertString(offset, text, attributes);
            }
        }
    }

    /**
     * Sets the flag to allow or disallow a zero value.
     * 
     * @param zeroAllowed true if a zero value should be allowed, false
     *            otherwise
     */
    public void setZeroAllowed(boolean zeroAllowed)
    {
        this.zeroAllowed = zeroAllowed;
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
     * Determines if all the chars in the specified text are digits.
     * 
     * @param text
     * @return
     * @see Character#isDigit(char)
     */
    protected boolean isNumeric(String text)
    {
        boolean numeric = true;
        int len = text.length();

        for (int i = 0; i < len; i++)
        {
            if (!Character.isDigit(text.charAt(i)))
            {
                numeric = false;
                break;
            }
        }
        return numeric;
    }
}
