/* ===========================================================================
* Copyright (c) 2004, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/IntegerTextField.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:42 mszekely Exp $
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
 *    4    360Commerce 1.3         4/25/2008 9:20:08 PM   Kun Lu          Fix
 *         CR 31129
 *    3    360Commerce 1.2         3/31/2005 4:28:23 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:22:09 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:11:26 PM  Robert Pearse   
 *
 *   Revision 1.1  2004/04/16 18:56:33  tfritz
 *   @scr 4251 - Integer parameters now can except negative and positive integers.
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.beans;

import java.text.DecimalFormat;
import javax.swing.text.Document;

import oracle.retail.stores.domain.utility.LocaleUtilities;
import oracle.retail.stores.foundation.utility.Util;

//--------------------------------------------------------------------------
/**
   This field allows input to be valid if it meets max and min length
   requirements and is a positive or negative number (integer).

    $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//--------------------------------------------------------------------------
public class IntegerTextField extends ConstrainedTextField
{
    /** revision number supplied by Team Connection */
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    //---------------------------------------------------------------------
    /**
     Constructor.
     */
    //---------------------------------------------------------------------
    public IntegerTextField()
    {
        this("");
    }

    //---------------------------------------------------------------------
    /**
     Constructor.
     @param value the default text for the field
     */
    //---------------------------------------------------------------------
    public IntegerTextField(String value)
    {
        this(value, 0, Integer.MAX_VALUE);
    }

    //---------------------------------------------------------------------
    /**
     Constructor.
     @param value the default text for the field
     @param minLength the minimum length for a valid field
     @param maxLength the maximum length for a valid field
     */
    //---------------------------------------------------------------------
    public IntegerTextField(String value, int minLength, int maxLength)
    {
        super(value, minLength, maxLength);
        setZeroAllowed(true);
    }


    //---------------------------------------------------------------------
    /**
     Constructor.
     @param value the default text for the field
     @param minLength the minimum length for a valid field
     @param maxLength the maximum length for a valid field
     @param isZeroAllowed whether a zero value is allowed or not
     */
    //---------------------------------------------------------------------
    public IntegerTextField(String value, int minLength, int maxLength, boolean isZeroAllowed)
    {
        super(value, minLength, maxLength);
        setZeroAllowed(isZeroAllowed);
    }
    //---------------------------------------------------------------------
    /**
     Gets the default model for the Constrained field
     @return the model for length constrained fields
     */
    //---------------------------------------------------------------------
    protected Document createDefaultModel()
    {
        return new IntegerDocument(Integer.MAX_VALUE);
    }

    //---------------------------------------------------------------------
    /**
     Gets the value as a long.
     @return the value of the text in the field as a long.
     @exception NumberFormatException if the value does not parse into
     a valid long
     */
    //---------------------------------------------------------------------
    public long getLongValue()
    {
        long value = 0;
        String text = getText();
        if (!Util.isEmpty(text))
        {
            if (getDocument() instanceof DecimalDocument)
            {
                DecimalDocument doc = (DecimalDocument)getDocument();
                // retrieve decimal format from document.
                DecimalFormat df = doc.getFormat();

                Number numValue = LocaleUtilities.parseNumber(text,df);
                if (numValue != null)
                {
                    value = numValue.longValue();
                }
            }
        }
        return value;
    }

    //---------------------------------------------------------------------
    /**
     Sets the value of the field from the long.
     @param value the value to set the field
     */
    //---------------------------------------------------------------------
    public void setLongValue(long value)
    {
        setText(Long.toString(value));
    }

    //---------------------------------------------------------------------
    /**
     Sets the flag to allow or disallow a zero value.
     @param zeroAllowed true if a zero value should be allowed,
     false otherwise
     */
    //---------------------------------------------------------------------
    public void setZeroAllowed(boolean zeroAllowed)
    {
        if (getDocument() instanceof IntegerDocument)
        {
            ((IntegerDocument)getDocument()).setZeroAllowed(zeroAllowed);
        }
    }
    //---------------------------------------------------------------------
    /**
     Returns whether a zero value is allowed in this field.
     @return true if zero value is allowed, false otherwise
     */
    //---------------------------------------------------------------------
    public boolean isZeroAllowed()
    {
        boolean rv = true;

        if (getDocument() instanceof IntegerDocument)
        {
            rv = ((IntegerDocument)getDocument()).isZeroAllowed();
        }

        return rv;
    }
}
