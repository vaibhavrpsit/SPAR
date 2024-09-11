/* ===========================================================================
* Copyright (c) 2003, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/NaPhoneNumField.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:40 mszekely Exp $
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
 * 3    360Commerce 1.2         3/31/2005 4:29:07 PM   Robert Pearse   
 * 2    360Commerce 1.1         3/10/2005 10:23:39 AM  Robert Pearse   
 * 1    360Commerce 1.0         2/11/2005 12:12:44 PM  Robert Pearse   
 *
 *Revision 1.4  2004/09/23 17:59:02  jdeleau
 *@scr 7028 The phone number field for the instant credit customer info screen 
 *is now alpha numeric.
 *
 *Revision 1.3  2004/03/16 17:15:18  build
 *Forcing head revision
 *
 *Revision 1.2  2004/02/11 20:56:27  rhafernik
 *@scr 0 Log4J conversion and code cleanup
 *
 *Revision 1.1.1.1  2004/02/11 01:04:22  cschellenger
 *updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.2   Nov 21 2003 16:43:20   nrao
 * Added new copyright style.
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.beans;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.text.BadLocationException;
import javax.swing.text.Document;

//-------------------------------------------------------------------------
/**
   This field allows phone numbers in the North American numbering plan,
   (NPA)NXX-XXXX to be input.
   @version $Revision: /rgbustores_13.4x_generic_branch/1 $
*/
//-------------------------------------------------------------------------
public class NaPhoneNumField extends ValidatingTextField
{
    /** revision number supplied by Team Connection */
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";
    
    /**
        Flag indicating that the area code should not be validated against
        starting with a minimum value.
    **/
    private boolean validateAreaCodeFirstDigit = true;

    //---------------------------------------------------------------------
    /**
       Constructor.
    */
    //---------------------------------------------------------------------
    public NaPhoneNumField()
    {
        this("");
        setMinLength(10);
    }

    //---------------------------------------------------------------------
    /**
       Constructor.
       @param number the phone number
    */
    //---------------------------------------------------------------------
    public NaPhoneNumField(String number)
    {
        super(number);
        if (getDocument() instanceof NaPhoneNumDocument)
        {
            NaPhoneNumDocument doc = (NaPhoneNumDocument)getDocument();
            ActionListener l = new ActionListener()
            {
                public void actionPerformed(ActionEvent evt)
                {
                    int pos;
                    if (evt.getID() == NaPhoneNumDocument.FIRST_DIGIT)
                    {
                        setCaretPosition(2);
                    }
                    else if (evt.getID() == NaPhoneNumDocument.NPA)
                    {
                        pos = getCaretPosition();
                        if (pos < getText().length())
                        {
                            setCaretPosition(pos + 2);
                        }
                    }  
                    else
                    {
                        pos = getCaretPosition();
                        if (pos < getText().length())
                        {
                            setCaretPosition(pos + 1);
                        }
                    }
                }
            };
            doc.addFieldListener(l);
        }
    }

    /**
     * Set whether alpha characters are permitted.
     * @since 7.0
     *  
     * @param val
     */
    public void setAlphaAccepted(boolean val)
    {
        if (getDocument() instanceof NaPhoneNumDocument)
        {
            NaPhoneNumDocument doc = (NaPhoneNumDocument)getDocument();
            doc.setAlphaAccepted(val);
        }
    }
    
    /**
     * Return whether or not alpha characters are accepted
     * @since 7.0
     * 
     * @return true or false
     */
    public boolean isAlphaAccepted()
    {
        boolean val = false;
        if (getDocument() instanceof NaPhoneNumDocument)
        {
            NaPhoneNumDocument doc = (NaPhoneNumDocument)getDocument();
            val = doc.isAlphaAccepted();
        }
        return val;
    }
    
    //---------------------------------------------------------------------
    /**
       Gets the default model for the decimal text field
       @return the model for length constrained decimal fields
    */
    //---------------------------------------------------------------------
    protected Document createDefaultModel()
    {
        return new NaPhoneNumDocument();
    }

    //---------------------------------------------------------------------
    /**
       Gets the phone number without the delimeters.
       @return the phone number without the delimeters
    */
    //---------------------------------------------------------------------
    public String getPhoneNumber()
    {
        String phn = "";
        if (getDocument() instanceof NaPhoneNumDocument)
        {
            NaPhoneNumDocument doc = (NaPhoneNumDocument)getDocument();
            phn = doc.getNpa() + doc.getNxx() + doc.getXxxx();
        }
        return phn;
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
        boolean rv = false;
        if (getPhoneNumber().length() == getMinLength())
        {
            rv = super.isInputValid();
        }
        return rv;
    }

    //---------------------------------------------------------------------
    /**
       Sets the phone number without the delimeters.
       @param phoneNumber the phone number without the delimeters
    */
    //---------------------------------------------------------------------
    public void setPhoneNumber(String phoneNumber)
    {
            setText(phoneNumber);
    }
    //---------------------------------------------------------------------
    /**
       Gets the phone format value. 
     */
    //---------------------------------------------------------------------
    public void setFormat(String value)
    {                                   // begin getEYSDate()
        if (getDocument() instanceof NaPhoneNumDocument)
        {                               // handle date document
            NaPhoneNumDocument doc = (NaPhoneNumDocument) getDocument();
            doc.setFormat(value);
        }                               // end handle date document
     }   

    //---------------------------------------------------------------------
    /**
       Gets the phone format value. 
     */
    //---------------------------------------------------------------------
    public String getFormat()
    {                                   
            return ((NaPhoneNumDocument) getDocument()).getFormat();
     }
    //---------------------------------------------------------------------
    /**
       Sets the text of the field.
       @param phoneNumber the phone number
    */
    //---------------------------------------------------------------------
    public void setText(String phoneNumber)
    {
        try
        {
            getDocument().remove(0, getText().length());
        }
        catch (BadLocationException excp)
        {
            super.setText("");
        }
        super.setText(phoneNumber);
    }
    
    //--------------------------------------------------------------------------
    /**
        Return the flag indicating validation of area code first digit. <P>
        @return true if the first digit is validated against a minimum 
    **/
    //--------------------------------------------------------------------------
    public boolean isValidateAreaCodeFirstDigit()
    {
        return validateAreaCodeFirstDigit;
    }

    //--------------------------------------------------------------------------
    /**
        Set the flag indicating validation of area code first digit. <P>
        @param set to true if the first digit is validated against a minimum 
    **/
    //--------------------------------------------------------------------------
    public void setValidateAreaCodeFirstDigit(boolean validate)
    {
        validateAreaCodeFirstDigit = validate;
        Document doc = getDocument();
        if ((doc != null) && (doc instanceof NaPhoneNumDocument))
        {
            ((NaPhoneNumDocument) doc).setValidateAreaCodeFirstDigit(validate);
        }
    }
}
