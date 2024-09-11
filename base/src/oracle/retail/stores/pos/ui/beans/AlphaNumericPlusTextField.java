/* ===========================================================================
* Copyright (c) 2011, 2014, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/AlphaNumericPlusTextField.java /main/2 2014/03/18 16:18:16 yiqzhao Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    yiqzhao   03/18/14 - Make allowable characters configurable.
 *    yiqzhao   03/17/14 - Allow hypen in item id and serial number.
 *    subrdey   02/14/13 - Allows AlphaNumeric character and Hyphen for Price
 *                         Inquiry Parameter.
 * 
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.beans;

import javax.swing.text.Document;

/**
 * An instance of AlphaNumericHyphenTextField is a AlphaNumericTextField that
 * uses a {@link AlphaNumericHyphenDocument}.
 * 
 * @author subrdey
 */
public class AlphaNumericPlusTextField extends AlphaNumericTextField
{   

    /**
     * Constructor.
     */
    public AlphaNumericPlusTextField()
    {
        this("");
    }
    
    /**
     * Constructor.
     * 
     * @param value the default text for the field
     */
    public AlphaNumericPlusTextField(String value)
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
    public AlphaNumericPlusTextField(String value, int minLength, int maxLength)
    {
        super(value, minLength, maxLength);
    }

    /**
     * Constructor that indicates if double byte chars are allowed in the field
     * or not. Added in I18N Phase 2 to indicate that some of the field should
     * not allow double bytes
     * 
     * @param value the default text for the field
     * @param minLength the minimum length for a valid field
     * @param maxLength the maximum length for a valid field
     * @param doubleBytesCharsAllowed boolean
     * @param allowableCharacters char...
     */
    public AlphaNumericPlusTextField(String value, int minLength, int maxLength, boolean doubleBytesCharsAllowed, char... allowableCharacters)
    {
        super(value, minLength, maxLength, doubleBytesCharsAllowed);   
        setAllowableCharacters(allowableCharacters); 
    }
    
    /**
     * Sets all the allowable characters to the document.
     * @param allowableCharacters
     */
    public void setAllowableCharacters(char... allowableCharacters)
    {
        ((AlphaNumericPlusDocument)getDocument()).setAllowableCharacters(allowableCharacters);
    }
    
    /**
     * Gets the default model for the Constrained field
     * 
     * @return the model for length constrained fields
     */
    protected Document createDefaultModel()
    { 
        return new AlphaNumericPlusDocument(Integer.MAX_VALUE);
    }
}
