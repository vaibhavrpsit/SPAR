/* ===========================================================================
* Copyright (c) 2007, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/NumericByteTextField.java /main/13 2012/05/08 12:57:39 vtemker Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    vtemker   05/08/12 - Fixed NPE issue in Taxpayer ID field (Budb ID:
 *                         14021736)
 *    cgreene   05/28/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *  3    360Commerce 1.2         4/14/2008 12:28:31 AM  Manikandan Chellapan
 *       CR#30812 Disabled cut and copy functions in NumericByteTextField.
 *  2    360Commerce 1.1         11/29/2007 5:15:58 PM  Alan N. Sinton  CR
 *       29677: Protect user entry fields of PAN data.
 *  1    360Commerce 1.0         11/13/2007 2:40:38 PM  Jack G. Swan    Added
 *       to support retrieving card numbers from UI as a byte array instead of
 *        a String object.
 * $
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
public class NumericByteTextField extends NumericTextField implements BytesRetrievableIfc
{
    /**  */
    private static final long serialVersionUID = 2506652260970723150L;
    
    /** revision number supplied by Team Connection */
    public static final String revisionNumber = "$Revision: /main/13 $";

    //---------------------------------------------------------------------
    /**
       Constructor.
    */
    //---------------------------------------------------------------------
    public NumericByteTextField()
    {
        this("");
    }

    //---------------------------------------------------------------------
    /**
       Constructor.
       @param value the default text for the field
    */
    //---------------------------------------------------------------------
    public NumericByteTextField(String value)
    {
        super(value, 0, Integer.MAX_VALUE);
    }

    //---------------------------------------------------------------------
    /**
       Constructor.
       @param value the default text for the field
       @param minLength the minimum length for a valid field
       @param maxLength the maximum length for a valid field
    */
    //---------------------------------------------------------------------
    public NumericByteTextField(String value, int minLength, int maxLength)
    {
        super(value, minLength, maxLength);
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
     public NumericByteTextField(String value, int minLength, int maxLength, boolean isZeroAllowed)
     {
         super(value, minLength, maxLength, isZeroAllowed);
     }
    //---------------------------------------------------------------------
    /**
       Gets the default model for the Constrained field
       @return the model for length constrained fields
    */
    //---------------------------------------------------------------------
    protected Document createDefaultModel()
    {
        return new NumericByteDocument(Integer.MAX_VALUE);
    }

    //---------------------------------------------------------------------
    /**
       Gets the entered text as bytes
       @return byte array
    */
    //---------------------------------------------------------------------
    public byte[] getTextBytes()
    {
        byte[] array = null;
        if (getDocument() instanceof BytesRetrievableIfc)
        {
            array = ((BytesRetrievableIfc)getDocument()).getTextBytes();
        }
        return array;
    }

    //---------------------------------------------------------------------
    /**
       Sets the text as bytes
       @param byte array
    */
    //---------------------------------------------------------------------
    public void setTextBytes(byte[] value)
    {
        if (getDocument() instanceof BytesRetrievableIfc)
        {
            ((BytesRetrievableIfc)getDocument()).setTextBytes(value);
        }
    }

    //---------------------------------------------------------------------
    /**
       Clears the content of currently held values.
    */
    //---------------------------------------------------------------------
    public void clearTextBytes()
    {
        if (getDocument() instanceof BytesRetrievableIfc)
        {
            ((BytesRetrievableIfc)getDocument()).clearTextBytes();
        }
    }
    
    //---------------------------------------------------------------------
    /**
     * Overriding copy() with an empty implementation in order to disable it
     * for the security of credit account numbers.
     *      * @see javax.swing.text.JTextComponent#copy()
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
    
    @Override
    /**
     * Determines whether the current field information is valid and returns the
     * result.
     * 
     * @return true if the current field entry is valid, false otherwise
     */
    public boolean isInputValid()
    {
        boolean isValidInput = true;
        byte[] array = getTextBytes();
        if (!emptyAllowed && (array == null || array.length == 0))
        {
            isValidInput = false;
        }
        else
        {
            if (array != null)
            {
                if (array.length < getMinLength())
                {
                    isValidInput = false;
                }
            }
        }
        return isValidInput;
    }
}
