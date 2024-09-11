/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/PercentageTextField.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:39 mszekely Exp $
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
 *    3    360Commerce 1.2         3/31/2005 4:29:20 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:24:02 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:13:02 PM  Robert Pearse   
 *
 *   Revision 1.3  2004/03/16 23:33:06  dcobb
 *   @scr 3911 Feature Enhancement: Markdown
 *   Code review modifications.
 *
 *   Revision 1.2  2004/03/16 17:15:18  build
 *   Forcing head revision
 *
 *   Revision 1.1  2004/03/09 19:22:27  epd
 *   @scr 0 Added new type of text field for entering values in percent
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.beans;

import javax.swing.text.Document;

import oracle.retail.stores.pos.ui.beans.DecimalTextField;
import oracle.retail.stores.pos.ui.beans.PercentageDocument;

//-------------------------------------------------------------------------
/**
   This field allows input to be valid if it meets max and min values (0.00 - 100.00)
 */
//-------------------------------------------------------------------------
public class PercentageTextField extends DecimalTextField
{
    /** revision number supplied by Team Connection */
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    //---------------------------------------------------------------------
    /**
       Constructor.
    */
    //---------------------------------------------------------------------
    public PercentageTextField()
    {
        this("");
    }

    //---------------------------------------------------------------------
    /**
       Constructor.
       @param value the default text for the field
    */
    //---------------------------------------------------------------------
    public PercentageTextField(String value)
    {
        this(value, 0, 100);
    }

    //---------------------------------------------------------------------
    /**
       Constructor.
       @param value the default text for the field
       @param minValue the minimum value for a valid field
       @param maxValue the maximum value for a valid field
    */
    //---------------------------------------------------------------------
    public PercentageTextField(String value, int minValue, int maxValue)
    {
        super(value);
        ((PercentageDocument)getDocument()).setMinValue(minValue);
        ((PercentageDocument)getDocument()).setMaxValue(maxValue);
    }

    //---------------------------------------------------------------------
    /**
       Gets the default model for the Constrained field
       @return the model for value constrained field
    */
    //---------------------------------------------------------------------
    protected Document createDefaultModel()
    {
        return new PercentageDocument();
    }

    //---------------------------------------------------------------------
    /**
       Returns the maximum value of a valid field.
       @return the maximum value of a valid field
    */
    //---------------------------------------------------------------------
    public double getMaxValue()
    {
        return (((PercentageDocument)getDocument()).getMaxValue());
    }

    //---------------------------------------------------------------------
    /**
       Returns the minimum value of a valid field.
       @return the minimum value of a valid field
    */
    //---------------------------------------------------------------------
    public double getMinValue()
    {
        return (((PercentageDocument)getDocument()).getMinValue());
    }

}
