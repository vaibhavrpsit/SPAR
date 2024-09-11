/* ===========================================================================
* Copyright (c) 1998, 2013, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/GovernmentIdField.java /main/13 2013/10/15 14:16:21 asinton Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    asinton   10/10/13 - removed references to social security number and
 *                         replaced with locale agnostic government id
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.beans;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.text.BadLocationException;
import javax.swing.text.Document;

/**
 * This field allows Government ID numbers, XXX-XX-XXXX, to be input.  Replaces
 * Social Security Field class.
 * @since 14.0
 */
public class GovernmentIdField extends ValidatingTextField
{
    /**
     * Constructor.
     */
    public GovernmentIdField()
    {
        this("");
    }

    /**
     * Constructor.
     * @param number the Government ID
     */
    public GovernmentIdField(String number)
    {
        super(number);
        if (getDocument() instanceof NaPhoneNumDocument)
        {
            GovernmentIdDocument doc = (GovernmentIdDocument)getDocument();
            ActionListener l = new ActionListener()
            {
                public void actionPerformed(ActionEvent evt)
                {
                    int pos = getCaretPosition();
                    if (evt.getID() == GovernmentIdDocument.PART1)
                    {
                        if (pos < getText().length())
                        {
                            setCaretPosition(pos + 1);
                        }
                    }
                    else if (evt.getID() == GovernmentIdDocument.PART2)
                    {
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
     * Gets the default model for the decimal text field
     * @return the model for length constrained decimal fields
     */
    protected Document createDefaultModel()
    {
        return new GovernmentIdDocument();
    }

    /**
     * Gets the Government ID without the delimeters.
     * <p/>
     * @return the government Id without the delimeters
     */
    public String getGovernmentIdNumber()
    {
        String soc = "";
        if (getDocument() instanceof GovernmentIdDocument)
        {
            GovernmentIdDocument doc = (GovernmentIdDocument)getDocument();
            soc = doc.getPart1() + doc.getPart2() + doc.getPart3();
        }
        return soc;
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
        if (getGovernmentIdNumber().length() == 9)
        {
            rv = true;
        }
        return rv;
    }

    /**
     * Sets the Government ID number without the delimeters.
     * @param governmentId the Government ID number without the delimeters
     */
    public void setGovernmentId(String governmentId)
    {
        setText(governmentId);
    }

    /**
     * Sets the text of the field.
     * @param governmentId the Government ID number
     */
    public void setText(String governmentId)
    {
        try
        {
            getDocument().remove(0, getText().length());
        }
        catch (BadLocationException excp)
        {
            super.setText("");
        }
        super.setText(governmentId);
    }
}
