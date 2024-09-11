/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/RestrictCutCopyTextField.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:54 mszekely Exp $
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.beans;

import javax.swing.text.Document;

//-------------------------------------------------------------------------
/**
 This field allows input to be valid if it meets max and min length
   requirements and is alpha numeric.  All input will be converted to
   uppercase.And it disable cut and copy of Secure Data.
   
*/
//-------------------------------------------------------------------------

public class RestrictCutCopyTextField extends AlphaNumericTextField {
	
	
	/**
     *  attribute for allowing or not spaces
     */
    protected boolean isSpaceAllowed = false;


    
    //---------------------------------------------------------------------
    /**
       Constructor.
    */
    //---------------------------------------------------------------------
    public RestrictCutCopyTextField()
    {
        this("");
    }

    //---------------------------------------------------------------------
    /**
       Constructor.
       @param value the default text for the field
    */
    //---------------------------------------------------------------------
    public RestrictCutCopyTextField(String value)
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
    public RestrictCutCopyTextField(String value, int minLength, int maxLength)
    {
        super(value, minLength, maxLength);
    }

    //---------------------------------------------------------------------
    /**
       Constructor that indicates if double byte chars are allowed in the field or not.
       Added in I18N Phase 2 to indicate that some of the field should not allow double bytes
       @param value the default text for the field
       @param minLength the minimum length for a valid field
       @param maxLength the maximum length for a valid field
       @param doubleBytesCharsAllowed boolean
    */
    //---------------------------------------------------------------------
    public RestrictCutCopyTextField(String value, int minLength, int maxLength, boolean doubleBytesCharsAllowed)
    {
        super(value, minLength, maxLength, doubleBytesCharsAllowed);

    }
    //--------------------------------------------------
    /**
       Gets the default model for the Constrained field
       @return the model for length constrained fields
    */
    //---------------------------------------------------------------------
    protected Document createDefaultModel()
    {
        return new AlphaNumericDocument(Integer.MAX_VALUE);
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
    //---------------------------------------------------------------------
    /**
     * Overriding copy() with an empty implementation in order to disable it
     * for the security of credit account numbers.
     *      * @see javax.swing.text.JTextComponent#cut()
     */
    //---------------------------------------------------------------------
    
 
    public void copy()
    {
        // do nothing
    }
    
    //---------------------------------------------------------------------
    /**
     * Overriding cut() with an empty implementation in order to disable it
     * for the security of credit account numbers.
     *      * @see javax.swing.text.JTextComponent#cut()
     */
    //---------------------------------------------------------------------
    public void cut()
    {
        // do nothing
    } 
}
