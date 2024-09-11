/* ===========================================================================
* Copyright (c) 2007, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/MaskableNumericByteTextField.java /rgbustores_13.4x_generic_branch/2 2011/06/17 13:32:52 cgreene Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   06/17/11 - add ability to treat contents like a masked credit
 *                         card number
 *    cgreene   05/28/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *  2    360Commerce 1.1         12/27/2007 10:39:29 AM Alan N. Sinton  CR
 *       29677: Check in changes per code review.  Reviews are Michael Barnett
 *        and Tony Zgarba.
 *  1    360Commerce 1.0         11/30/2007 1:24:39 AM  Alan N. Sinton  CR
 *       29677: To support protection of PAN data when swiped from MSR.
 * $
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.beans;

import javax.swing.text.Document;

/**
 * Extended NumericByteTextField to allow a mask character to be used.
 * $Revision: /rgbustores_13.4x_generic_branch/2 $
 */
public class MaskableNumericByteTextField extends NumericByteTextField
{
    /** serialVersionUID to appease compiler. */
    private static final long serialVersionUID = 2506652260970723150L;

    /** revision number supplied by Team Connection */
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/2 $";

    /**
     * Constructor.
     */
    public MaskableNumericByteTextField()
    {
        this("");
    }

    /**
     * Constructor.
     * 
     * @param value the default text for the field
     */
    public MaskableNumericByteTextField(String value)
    {
        super(value, 0, Integer.MAX_VALUE);
    }

    /**
     * Constructor.
     * 
     * @param value the default text for the field
     * @param minLength the minimum length for a valid field
     * @param maxLength the maximum length for a valid field
     */
    public MaskableNumericByteTextField(String value, int minLength, int maxLength)
    {
        super(value, minLength, maxLength);
    }

    /**
     * Constructor.
     * 
     * @param value the default text for the field
     * @param minLength the minimum length for a valid field
     * @param maxLength the maximum length for a valid field
     * @param isZeroAllowed whether a zero value is allowed or not
     */
    public MaskableNumericByteTextField(String value, int minLength, int maxLength, boolean isZeroAllowed)
    {
        super(value, minLength, maxLength, isZeroAllowed);
    }

    /**
     * Gets the default model for the Constrained field. The max length should
     * be set in a super constructor.
     * 
     * @return the model for length constrained fields
     */
    protected Document createDefaultModel()
    {
        return new MaskableNumericByteDocument(Integer.MAX_VALUE);
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
        byte[] array = getTextBytes();
        if (emptyAllowed == false && (array == null || array.length == 0))
        {
            rv = false;
        }
        else
        {
            if (array != null)
            {
                if (array.length < getMinLength())
                {
                    rv = false;
                }
            }
        }
        return rv;
    }

    /**
     * Return true if this field should treat its contents like a card number,
     * i.e. whether it will mask the middle six chars.
     * 
     * @return the cardNumber
     */
    public boolean isCardNumber()
    {
        if (getDocument() instanceof MaskableNumericByteDocument)
        {
            return ((MaskableNumericByteDocument)getDocument()).isCardNumber();
        }
        return false;
    }

    /**
     * Set whether this field should treat its contents like a card number, i.e.
     * whether it will mask the middle six chars.
     * 
     * @param cardNumber the cardNumber to set
     */
    public void setCardNumber(boolean cardNumber)
    {
        Document doc = getDocument();
        if (doc instanceof MaskableNumericByteDocument)
        {
            MaskableNumericByteDocument model = (MaskableNumericByteDocument)doc;
            model.setCardNumber(cardNumber);
        }
    }

}