/* ===========================================================================
* Copyright (c) 2008, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/NumericTextField.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:57 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 * 3    360Commerce 1.2         3/31/2005 4:29:10 PM   Robert Pearse   
 * 2    360Commerce 1.1         3/10/2005 10:23:44 AM  Robert Pearse   
 * 1    360Commerce 1.0         2/11/2005 12:12:48 PM  Robert Pearse   
 *
 *Revision 1.2  2004/03/16 17:15:18  build
 *Forcing head revision
 *
 *Revision 1.1.1.1  2004/02/11 01:04:22  cschellenger
 *updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 16:11:18   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.1   Jul 22 2003 10:38:34   baa
 * add zeroAllow property
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.beans;

import javax.swing.text.Document;

//-------------------------------------------------------------------------
/**
   This field allows input to be valid if it meets max and min lenght
   requirements.
*/
//-------------------------------------------------------------------------
public class NumericTextField extends ConstrainedTextField
{
    /** revision number supplied by Team Connection */
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    //---------------------------------------------------------------------
    /**
       Constructor.
    */
    //---------------------------------------------------------------------
    public NumericTextField()
    {
        this("");
    }

    //---------------------------------------------------------------------
    /**
       Constructor.
       @param value the default text for the field
    */
    //---------------------------------------------------------------------
    public NumericTextField(String value)
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
    public NumericTextField(String value, int minLength, int maxLength)
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
     public NumericTextField(String value, int minLength, int maxLength, boolean isZeroAllowed)
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
        return new NumericDocument(Integer.MAX_VALUE);
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
        return Long.parseLong(getText());
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
        if (getDocument() instanceof NumericDocument)
        {
            ((NumericDocument)getDocument()).setZeroAllowed(zeroAllowed);
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

        if (getDocument() instanceof NumericDocument)
        {
            rv = ((NumericDocument)getDocument()).isZeroAllowed();
        }

        return rv;
    }
    
}
