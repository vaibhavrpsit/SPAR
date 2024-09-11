/* ===========================================================================
* Copyright (c) 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/MultipleQuantityTextField.java /rgbustores_13.4x_generic_branch/1 2011/03/21 16:19:32 cgreene Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   03/16/11 - implement You Saved feature on reciept and
 *                         AllowMultipleQuantity parameter
 *    cgreene   03/15/11 - initial version
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.beans;

import javax.swing.text.Document;

/**
 * An instance of MultipleQuantityTextField is a AlphaNumericTextField that
 * uses a {@link MultipleQuantityDocument}.
 * 
 * @author cgreene
 * @since 13.4
 * @version $Revision: /rgbustores_13.4x_generic_branch/1 $
 */
public class MultipleQuantityTextField extends AlphaNumericTextField
{
    private static final long serialVersionUID = -762394964057386235L;

    /**
     * Constructor.
     */
    public MultipleQuantityTextField()
    {
        this("");
    }

    /**
     * Constructor.
     * 
     * @param value the default text for the field
     */
    public MultipleQuantityTextField(String value)
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
    public MultipleQuantityTextField(String value, int minLength, int maxLength)
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
     */
    public MultipleQuantityTextField(String value, int minLength, int maxLength, boolean doubleBytesCharsAllowed)
    {
        super(value, minLength, maxLength, doubleBytesCharsAllowed);

    }

    /**
     * Gets the default model for the Constrained field
     * 
     * @return the model for length constrained fields
     */
    protected Document createDefaultModel()
    {
        return new MultipleQuantityDocument(Integer.MAX_VALUE);
    }

}
