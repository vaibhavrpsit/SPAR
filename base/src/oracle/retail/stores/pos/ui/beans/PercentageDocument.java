/* ===========================================================================
* Copyright (c) 1998, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/PercentageDocument.java /main/16 2012/07/10 11:04:21 icole Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    icole     06/28/12 - Forward Port: DECIMAL VALUE CAN NOT BE ENTERED IN
 *                         TAX OVERRIDE AND DISCOUNT AMOUNT.
 *    cgreene   05/28/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    4    360Commerce 1.3         4/17/2007 8:08:24 PM   Ashok.Mondal    CR
 *         3966 : V7.2.2 merge to trunk.
 *    3    360Commerce 1.2         3/31/2005 4:29:20 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:24:02 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:13:01 PM  Robert Pearse   
 *
 *   Revision 1.6  2004/09/23 00:07:11  kmcbride
 *   @scr 7211: Adding static serialVersionUIDs to all POS Serializable objects, minus the JComponents
 *
 *   Revision 1.5  2004/03/22 19:27:00  cdb
 *   @scr 3588 Updating javadoc comments
 *
 *   Revision 1.4  2004/03/16 23:33:06  dcobb
 *   @scr 3911 Feature Enhancement: Markdown
 *   Code review modifications.
 *
 *   Revision 1.3  2004/03/16 17:15:18  build
 *   Forcing head revision
 *
 *   Revision 1.2  2004/03/10 19:36:14  cdb
 *   @scr 3588 Modified percent entry bean to use PercentageTextField.
 *   Updated PercentageDocument to allow whole numbers only.
 *
 *   Revision 1.1  2004/03/09 19:22:27  epd
 *   @scr 0 Added new type of text field for entering values in percent
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.beans;

import java.text.DecimalFormatSymbols;
import java.util.Locale;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;

import oracle.retail.stores.pos.ui.beans.DecimalDocument;

//-------------------------------------------------------------------------
/**
   This document allows input to be valid if it meets minimum and maximum
   value requirements and is numeric.

   $Revision: /main/16 $
*/
//-------------------------------------------------------------------------
public class PercentageDocument extends DecimalDocument
{
    // This id is used to tell
    // the compiler not to generate a
    // new serialVersionUID.
    //
    static final long serialVersionUID = 1856735876906459292L;

    /** revision number */
    public static final String revisionNumber = "$Revision: /main/16 $";

    /**
     * Minimum percentage value
     */
    protected double minValue = 0;

    /**
     * Maximum percentage value
     */
    protected double maxValue = 100.0;

    //---------------------------------------------------------------------
    /**
       Constructor.
     */
    //---------------------------------------------------------------------
    public PercentageDocument()
    {
        setDecimalLength(3);
    }

    //---------------------------------------------------------------------
    /**
       Constructor.
       @param maxValue the maximum length
     */
    //---------------------------------------------------------------------
    public PercentageDocument(int maxValue)
    {
        this.maxValue = maxValue;
        setDecimalLength(3);
    }

    //---------------------------------------------------------------------
    /**
       Constructor.
       @param minValue the minimum value
       @param maxValue the maximum value
     */
    //---------------------------------------------------------------------
    public PercentageDocument(int minValue, int maxValue)
    {
        this.minValue = minValue;
        this.maxValue = maxValue;
        setDecimalLength(3);
    }

    //---------------------------------------------------------------------
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
        DecimalFormatSymbols defaultSymbols = new DecimalFormatSymbols(Locale.getDefault());
        boolean badNumber = false;
        if (text != null)
        {
            if (!badNumber)
            {
                double testValue = -1;
                try
                {
                    String enteredValue = getText(0, getLength());
                    if (enteredValue.equals("100")
                        && !Character.isDigit(text.charAt(text.length() - 1)))
                    {
                        badNumber = true;
                    }
                    enteredValue += text;
                    if (enteredValue.indexOf(symbols.getDecimalSeparator()) != -1)
                    {
                        if(enteredValue.indexOf(defaultSymbols.getDecimalSeparator()) == -1)
                        {
                            enteredValue = enteredValue.replace(symbols.getDecimalSeparator(), defaultSymbols.getDecimalSeparator());
                        }
                    }
                    testValue = new Double(enteredValue).doubleValue();
                }
                catch (NumberFormatException e)
                {
                    badNumber = true;
                }
                if (!badNumber &&
                    testValue <= maxValue &&
                    testValue >= minValue)
                {
                    // ensure separator matches locale as on a redisplay of a screen the separator is the default
                    String tempText = text;
                    if (tempText.indexOf(defaultSymbols.getDecimalSeparator()) != -1)
                    {
                        tempText = tempText.replace(defaultSymbols.getDecimalSeparator(), symbols.getDecimalSeparator());
                    }
                    super.insertString(offset, tempText, attributes);
                }
            }
        }
    }

    //---------------------------------------------------------------------
    /**
     * Sets the minimum value
     * @param minValue The minimum value
     */
    //---------------------------------------------------------------------
    public void setMinValue(double minValue)
    {
        this.minValue = minValue;
    }

    //---------------------------------------------------------------------
    /**
     * Returns the minimum value.
     * @return the minimum value
     */
    //---------------------------------------------------------------------
    public double getMinValue()
    {
        return this.minValue;
    }

    //---------------------------------------------------------------------
    /**
     * Sets the Maximum value
     * @param maxValue The maximum value
     */
    //---------------------------------------------------------------------
    public void setMaxValue(double maxValue)
    {
        this.maxValue = maxValue;
    }

    //---------------------------------------------------------------------
    /**
     * Returns the Maximum value
     * @return the maximum value
     */
    //---------------------------------------------------------------------
    public double getMaxValue()
    {
        return this.maxValue;
    }

}
