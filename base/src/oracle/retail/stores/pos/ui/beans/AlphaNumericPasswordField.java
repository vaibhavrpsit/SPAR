/* ===========================================================================
* Copyright (c) 1999, 2013, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/AlphaNumericPasswordField.java /main/15 2013/04/04 14:36:40 abhineek Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    abhineek  04/03/13 - fix for user is not prompted with any error message
 *                         when a special character is entered in the password
 *                         field
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *    glwang    02/25/09 - remove special characters from the password fields.
 *
 * ===========================================================================
 * $Log:
 *  3    360Commerce 1.2         3/31/2005 4:27:12 PM   Robert Pearse   
 *  2    360Commerce 1.1         3/10/2005 10:19:35 AM  Robert Pearse   
 *  1    360Commerce 1.0         2/11/2005 12:09:27 PM  Robert Pearse   
 * $
 * Revision 1.1  2004/07/05 16:38:20  aachinfiev
 * @scr 5215 - Password accepting non-alphanumeric characters
 *
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.beans;

import javax.swing.text.Document;
import oracle.retail.stores.foundation.utility.Util;

//-------------------------------------------------------------------------
/**
    This field allows input to be valid if it meets max and min length
    requirements and is alpha numeric.
    @version $Revision: /main/15 $;
*/
//-------------------------------------------------------------------------
public class AlphaNumericPasswordField extends ValidatingPasswordField
{
    /**
     *  revision number
     */
    public static final String revisionNumber = "$Revision: /main/15 $";
    
    /**
     *  atribute for allowing or not spaces
     */
    protected boolean isSpaceAllowed = false; 
    
    /**
     * atribute for allowing or not specialCharacters
     */
    protected boolean isSpecialCharAllowed = false;
    
    //---------------------------------------------------------------------
    /**
       Constructor.
    */
    //---------------------------------------------------------------------
    public AlphaNumericPasswordField()
    {
        this("");
    }
    
    //---------------------------------------------------------------------
    /**
       Constructor.
       @param value the default text for the field
    */
    //---------------------------------------------------------------------
    public AlphaNumericPasswordField(String value)
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
    public AlphaNumericPasswordField(String value, int minLength, int maxLength)
    {
        super();
        setName(value);
        setMinLength(minLength);
       ((AlphaNumericDocument)getDocument()).setMaxLength(maxLength);
    }
    
    //--------------------------------------------------
    /**
       Gets the default model for the Constrained field
       @return the model for length constrained fields
    */
    //---------------------------------------------------------------------
    protected Document createDefaultModel()
    {
    	AlphaNumericDocument doc = new AlphaNumericDocument(Integer.MAX_VALUE);
    	doc.setSpaceAllowed(false);
        return doc;
    }
    
    //---------------------------------------------------------------------
    /**
       Returns default display string. <P>
       @return String representation of object
    */
    //---------------------------------------------------------------------
    public String toString()
    {
        String strResult = new String("Class: AlphaNumericPasswordField (Revision " +
                                      getRevisionNumber() + ") @" +
                                      hashCode());
        return(strResult);
    }
    
    //---------------------------------------------------------------------
    /**
       Returns the maximum length of a valid field.
       @return the maximum length of a valid field
    */
    //---------------------------------------------------------------------
    public int getMaxLength()
    {
        return ((AlphaNumericDocument)getDocument()).getMaxLength();
    }

    //---------------------------------------------------------------------
    /**
       Determines whether the current field information is valid and
       returns the result.
       @return true if the current field entry is valid, false otherwise
    */
    //---------------------------------------------------------------------
    public boolean isInputValid()
    {
        boolean rv = true;
        int len = getPassword().length;
        AlphaNumericDocument doc = (AlphaNumericDocument)getDocument();
        if (!super.isInputValid() ||
            len < getMinLength() ||
            len > doc.getMaxLength())
        {
            rv = false;
            // clear entered value if not valid
            setText("");
        }
        return rv;
    }

    //---------------------------------------------------------------------
    /**
       Sets the maximum length of a valid field.
       @param maxLength the maximum length for a valid field
    */
    //---------------------------------------------------------------------
    public void setMaxLength(int value)
    {
        ((AlphaNumericDocument)getDocument()).setMaxLength(value);
       
    }
    //---------------------------------------------------------------------
    /**
     * Gets isSpaceAllowed flag.
     * @return Returns the isSpaceAllowed.
     */
    //---------------------------------------------------------------------    
    public boolean isSpaceAllowed()
    {
        return isSpaceAllowed;
    }
    
    //---------------------------------------------------------------------    
    /**
     * Sets isSpaceAllowed flag.
     * @param value set the space allowed flag
     */
    //---------------------------------------------------------------------    
    public void setSpaceAllowed(boolean value)
    {
        ((AlphaNumericDocument)getDocument()).setSpaceAllowed(value);
    }
    
 // ---------------------------------------------------------------------
    /**
     * Gets isSpecialCharAllowed flag.
     *
     * @return Returns the isSpecialCharAllowed.
     */
    // ---------------------------------------------------------------------
    public boolean isSpecialCharAllowed()
    {
        return isSpecialCharAllowed;
    }

    // ---------------------------------------------------------------------
    /**
     * Sets isSpecialCharAllowed flag.
     *
     * @param value set the space allowed flag
     */
    // ---------------------------------------------------------------------
    public void setSpecialCharAllowed(boolean value)
    {
        ((AlphaNumericDocument)getDocument()).setSpecialCharAllowed(value);
    }
    
    //---------------------------------------------------------------------
    /**
       Retrieves the Team Connection revision number. <P>
       @return String representation of revision number
    */
    //---------------------------------------------------------------------
    public String getRevisionNumber()
    {
        return(Util.parseRevisionNumber(revisionNumber));
    }
}
