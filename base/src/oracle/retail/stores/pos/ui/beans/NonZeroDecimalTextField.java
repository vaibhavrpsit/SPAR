/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/NonZeroDecimalTextField.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:47 mszekely Exp $
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
 * $Log:
 *   4    360Commerce 1.3         1/22/2006 11:45:27 AM  Ron W. Haight
 *        removed references to com.ibm.math.BigDecimal
 *   3    360Commerce 1.2         3/31/2005 4:29:09 PM   Robert Pearse   
 *   2    360Commerce 1.1         3/10/2005 10:23:41 AM  Robert Pearse   
 *   1    360Commerce 1.0         2/11/2005 12:12:45 PM  Robert Pearse   
 *
 *  Revision 1.2  2004/03/16 17:15:18  build
 *  Forcing head revision
 *
 *  Revision 1.1.1.1  2004/02/11 01:04:22  cschellenger
 *  updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.1   Oct 29 2003 14:11:26   baa
 * refactor decimal formatting
 * Resolution for 3392: 610/700 Cleanup
 * 
 *    Rev 1.0.1.0   Oct 28 2003 13:43:22   baa
 * refactor  decimal formatting
 * 
 *    Rev 1.0   Aug 29 2003 16:11:16   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.1   Sep 23 2002 13:26:46   baa
 * fix decimal field
 * Resolution for POS SCR-1740: Code base Conversions
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.beans;

import java.math.BigDecimal;

//-------------------------------------------------------------------------
/**
   This field allows input to be valid if it meets max and min length
   requirements and is positive.  Should probably be renamed to
   PositiveDecimalTextField.
   @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//-------------------------------------------------------------------------
public class NonZeroDecimalTextField extends DecimalTextField
{
    /** revision number **/
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    //---------------------------------------------------------------------
    /**
        Class Constructor.
    **/
    //---------------------------------------------------------------------
    public NonZeroDecimalTextField()
    {
        this("");
    }

    //---------------------------------------------------------------------
    /**
        Class Constructor.
        @param value the default text for the field
    **/
    //---------------------------------------------------------------------
    public NonZeroDecimalTextField(String value)
    {
        this(value, Integer.MAX_VALUE, 2);
    }

    //---------------------------------------------------------------------
    /**
        Class Constructor.
        @param value the default text for the field
        @param minLength the minimum length for a valid field
        @param maxLength the maximum length for a valid field
    **/
    //---------------------------------------------------------------------
    public NonZeroDecimalTextField(String value, int maxLength, int decLength)
    {
        super(value, maxLength, decLength);
        setNegativeAllowed(false);
    }

    //---------------------------------------------------------------------
    /**
        Determines whether the current field information is valid and
        returns the result.
        @return true if the current field entry is valid, false otherwise
    **/
    //---------------------------------------------------------------------
    public boolean isInputValid()
    {
        // reset error message in case we've changed it
        setErrorMessage();

        boolean rv = false;
        if (super.isInputValid())
        {
            String text = getText();
            BigDecimal value = getDecimalValue();
            if (text.length() > 0 && value.signum() > 0)
            {
                rv = true;
            }
            else
            {
                setErrorMessage(getFieldName() + " must be greater than zero");
            }
        }
        return rv;
    }
}
