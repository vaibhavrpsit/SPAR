/* ===========================================================================
* Copyright (c) 1998, 2013, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/ConstrainedTextField.java /main/15 2013/06/04 17:39:14 cgreene Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   06/04/13 - removed toString method since it is less useful than
 *                         the normal toString
 *    cgreene   09/09/11 - deprecated
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *   8    360Commerce 1.7         2/4/2008 12:09:49 PM   Charles D. Baker CR
 *        29652 - Repaired ConstrainedTextField to permit insertString
 *        override of insertString in implementing classes. Code reviewed by
 *        Sandy Gu.
 *   8    I18N_P2    1.6.1.0     12/26/2007 9:54:39 AM  Maisa De Camargo CR
 *        29822 - I18N - Fixed Collapsing of Input Fields when labels are
 *        expanded.
 *   7    360Commerce 1.6         10/10/2007 1:02:52 PM  Anda D. Cadar
 *        changes to not allow double byte chars
 *   6    360Commerce 1.5         10/9/2007 1:45:44 PM   Maisa De Camargo
 *        Updated setText method to set the value of the TextField without
 *        doing any validation. This will mimic the WebApps behavior.
 *        Note that when we enter the data into the field by using the
 *        keyboard, the maxLength will still be taken into consideration.
 *   5    360Commerce 1.4         10/9/2007 10:22:03 AM  Anda D. Cadar   Fixed
 *        null pointer exception
 *   4    360Commerce 1.3         10/4/2007 3:39:15 PM   Maisa De Camargo
 *        Created the setText Method.
 *        This method truncates the text to the maxLength.
 *        Without this change, NO data was being displayed in the JText Field
 *        when text > maxLength.
 *   3    360Commerce 1.2         3/31/2005 4:27:30 PM   Robert Pearse   
 *   2    360Commerce 1.1         3/10/2005 10:20:23 AM  Robert Pearse   
 *   1    360Commerce 1.0         2/11/2005 12:10:11 PM  Robert Pearse   
 *
 *  Revision 1.5  2004/04/09 13:59:07  cdb
 *  @scr 4206 Cleaned up class headers for logs and revisions.
 *
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.beans;

import javax.swing.text.Document;

import oracle.retail.stores.common.utility.Util;

/**
 * This field allows input to be valid if it meets max and min length
 * requirements.
 * 
 * @version $KW=@(#); $Ver=pos_4.5.0:2; $EKW;
 * @deprecated as of 13.4. Use {@link oracle.retail.stores.foundation.manager.ui.jfc.ConstrainedTextField} instead.
 */
public class ConstrainedTextField extends ValidatingTextField
{
    /** Generated SerialVersionUID */
    private static final long serialVersionUID = -4904168717331593588L;
    
    /** revision number supplied by version control */
    public static final String revisionNumber = "$Revision: /main/15 $";

    /**
     * Constructor.
     */
    public ConstrainedTextField()
    {
        this("");
    }

    /**
     * Constructor.
     * 
     * @param value the default text for the field
     */
    public ConstrainedTextField(String value)
    {
        this(value, 0, Integer.MAX_VALUE);
    }

    /**
     * Constructor.
     * 
     * @param value the default text for the field
     * @param minLength the minimum length for a valid field
     * @param maxLength the maximum length for a valid field
     */
    public ConstrainedTextField(String value, int minLength, int maxLength)
    {
        super(value);
        setMinLength(minLength);
        ((ConstrainedTextDocument)getDocument()).setMaxLength(maxLength);
    }

    /**
     * Constructor.
     * 
     * @param value the default text for the field
     * @param minLength the minimum length for a valid field
     * @param maxLength the maximum length for a valid field
     * @param doubleByteCharsAllowed indicates if double byte chars are allowed
     */
    public ConstrainedTextField(String value, int minLength, int maxLength, boolean doubleByteCharsAllowed)
    {
        super(value);
        setMinLength(minLength);
        ((ConstrainedTextDocument)getDocument()).setMaxLength(maxLength);
        ((ConstrainedTextDocument)getDocument()).setDoubleByteCharsAllowed(doubleByteCharsAllowed);
    }

    /**
     * Gets the default model for the Constrained field
     * 
     * @return the model for length constrained fields
     */
    protected Document createDefaultModel()
    {
        return new ConstrainedTextDocument(Integer.MAX_VALUE);
    }

    /**
     * Returns the maximum length of a valid field.
     * 
     * @return the maximum length of a valid field
     */
    public int getMaxLength()
    {
        return ((ConstrainedTextDocument)getDocument()).getMaxLength();
    }

    /**
     * Determines whether the current field information is valid and returns the
     * result.
     * 
     * @return true if the current field entry is valid, false otherwise
     */
    public boolean isInputValid()
    {
        boolean rv = true;
        if (!super.isInputValid())
        {
            rv = false;
        }
        else
        {
            int len = 0;
            if (getText() != null)
            {
                len = getText().trim().length();
            }
            if (len < getMinLength())
            {
                rv = false;
            }
        }
        return rv;
    }

    /**
     * Sets the maximum length of a valid field.
     * 
     * @param maxLength the maximum length for a valid field
     */
    public void setMaxLength(int maxLength)
    {
        ((ConstrainedTextDocument)getDocument()).setMaxLength(maxLength);
    }

    /**
     * Retrieves the Team Connection revision number.
     * 
     * @return String representation of revision number
     */
    public String getRevisionNumber()
    {
        return (Util.parseRevisionNumber(revisionNumber));
    }

    /**
     * This method overrides the basic behavior of the JText.setText() If the
     * input string is bigger than the field maxLength, truncates the data This
     * method was created because if we had a text which was bigger than the
     * maxLength, nothing was being displayed in the field.
     * 
     * @param t text to be set in the UI Element
     */
    public void setText(String t)
    {
        
        if (t == null)
            t = new String ("");
        
        Document doc = getDocument();
        if (doc instanceof ConstrainedTextDocument)
        {
            ((ConstrainedTextDocument) doc).setBypassOfMaxLengthValidation(true);
            try
            {
                super.setText(t);
            }
            finally
            {
                ((ConstrainedTextDocument) doc).setBypassOfMaxLengthValidation(false);
            }
        }
        else
        {
            super.setText(t);
        }
    }
    
    /**
     * Sets the double byte char flag
     * 
     * @param doubleByteCharsAllowed if double byte chars are allowed
     */
    public void setDoubleByteCharsAllowed(boolean doubleByteCharsAllowed)
    {
    	((ConstrainedTextDocument)getDocument()).setDoubleByteCharsAllowed(doubleByteCharsAllowed);
    }
}
