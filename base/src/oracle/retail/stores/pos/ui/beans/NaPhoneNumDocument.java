/* ===========================================================================
* Copyright (c) 2003, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/NaPhoneNumDocument.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:45 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/28/10 - convert to oracle packaging
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
 *Revision 1.5  2004/09/23 17:59:02  jdeleau
 *@scr 7028 The phone number field for the instant credit customer info screen 
 *is now alpha numeric.
 *
 *Revision 1.4  2004/09/23 00:07:11  kmcbride
 *@scr 7211: Adding static serialVersionUIDs to all POS Serializable objects, minus the JComponents
 *
 *Revision 1.3  2004/03/16 17:15:18  build
 *Forcing head revision
 *
 *Revision 1.2  2004/02/11 20:56:26  rhafernik
 *@scr 0 Log4J conversion and code cleanup
 *
 *Revision 1.1.1.1  2004/02/11 01:04:22  cschellenger
 *updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.2   Nov 21 2003 16:42:52   nrao
 * Added new copyright style.
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.beans;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.event.EventListenerList;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

//-------------------------------------------------------------------------
/**
   This document controls a text field to ensure that input can only be
   a North American phone number, (NPA)NXX-XXXX.

   @version $Revision: /rgbustores_13.4x_generic_branch/1 $
*/
//-------------------------------------------------------------------------
class NaPhoneNumDocument extends PlainDocument
{
    // This id is used to tell
    // the compiler not to generate a
    // new serialVersionUID.
    //
    static final long serialVersionUID = 5675325638429829911L;

    /** revision number supplied by Team Connection */
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";
    /** list of objects interested in this class */
    protected EventListenerList listenerList;
    /** Constant for part 1 of phone number */
    public static final int NPA = 0;
    /** Constant for part 2 of phone number */
    public static final int NXX = 1;
    /** Constant for part 3 of phone number */
    public static final int XXXX = 2;
    /** Constant for part 4 of phone number */
    public static final int FIRST_DIGIT = 3;

    protected String format = "(000) 000-0000";
    
    /**
        Flag indicating that the area code should not be validated against
        starting with a minimum value.
    **/
    private boolean validateAreaCodeFirstDigit = true;
    private boolean alphaAccepted = false;

    //---------------------------------------------------------------------
    /**
       Constructor.
    */
    //---------------------------------------------------------------------
    public NaPhoneNumDocument()
    {
        super();
        listenerList = new EventListenerList();
    }

    //---------------------------------------------------------------------
    /**
       Add a listener to be notified when a field has been completed.
       @param l the listener to add
    */
    //---------------------------------------------------------------------
    public void addFieldListener(ActionListener l) 
    {
        listenerList.add(ActionListener.class, l);
    }

    //---------------------------------------------------------------------
    /**
       Moves the caret inside the parentheses.
    */
    //---------------------------------------------------------------------
    protected void fireFirstDigit() throws BadLocationException
    {
        fireFieldEvent(FIRST_DIGIT);
    }

    //---------------------------------------------------------------------
    /**
       Moves the caret past the parentheses.
    */
    //---------------------------------------------------------------------
    protected void fireNpaEntered() throws BadLocationException
    {
        fireFieldEvent(NPA);
    }

    //---------------------------------------------------------------------
    /**
       Notify all listeners that have registered interest for
       notification on this event type.
       @param field the field that was completed
    */
    //---------------------------------------------------------------------
    protected void fireFieldEvent(int field)
    {
        // Guaranteed to return a non-null array
        Object[] listeners = listenerList.getListenerList();
        // Process the listeners last to first, notifying
        // those that are interested in this event
        for (int i = listeners.length-2; i>=0; i-=2)
        {
            if (listeners[i]==ActionListener.class)
            {
                ActionEvent fieldEvent = new ActionEvent(this,
                                                         field,
                                                         "FieldCompleted");
                ((ActionListener)listeners[i+1]).actionPerformed(fieldEvent);
            }
        }
    }

    //---------------------------------------------------------------------
    /**
       Gets the numbering plan area, or area code.
       @return the numbering plan area, or area code
    */
    //---------------------------------------------------------------------
    public String getNpa()
    {
        String text;
        try
        {
            text = getText(0, getLength());
        }
        catch (BadLocationException excp)
        {
            text = "";
        }
        int start = text.indexOf('(');
        int end = text.indexOf(") "); //don't add one!!!
        if (end < 0)
        {
            end = getLength();
        }
        return text.substring(start + 1, end);
    }

    //---------------------------------------------------------------------
    /**
       Gets the exchange code
       @return the exchange code
    */
    //---------------------------------------------------------------------
    public String getNxx()
    {
        String text;
        try
        {
            text = getText(0, getLength());
        }
        catch (BadLocationException excp)
        {
            text = "";
        }
        int start = text.indexOf(") ") + 1;
        int end = text.indexOf('-');
        String rv = "";
        if (start > 0)
        {
            if (end == -1)
            {
                end = getLength();
            }
            rv = text.substring(start + 1, end);
        }
        return rv;
    }

    //---------------------------------------------------------------------
    /**
       Gets the last four digits of the number.
       @return the last four digits of the number
    */
    //---------------------------------------------------------------------
    public String getXxxx()
    {
        String text;
        try
        {
            text = getText(0, getLength());
        }
        catch (BadLocationException excp)
        {
            text = "";
        }
        int start = text.indexOf('-');
        String rv = "";
        if (start > -1)
        {
            rv = text.substring(start + 1, getLength());
        }
        return rv;
    }

    //---------------------------------------------------------------------
    /**
       Gets the format for the date.
       @return a pattern string for the current date
    */
    //---------------------------------------------------------------------
    public String getFormat()
    {
        return format;
    }
    
   //---------------------------------------------------------------------
    /**
       Gets the format for the date.
       @return a pattern string for the current date
    */
    //---------------------------------------------------------------------
    public void setFormat(String value)
    {
        format = value;
    }
    //---------------------------------------------------------------------
    /**
       Determines if the text can be inserted.
       <b>Pre-conditions</b>
       <ul>
       <li> </li>
       </ul>
       <b>Post-conditions</b>
       <ul>
       <li> </li>
       </ul>
       @param offset the offset at which the text should be inserted
       @param text the text to be inserted
       @param attributes the set of attributes for the text
       @exception BadLocationException if the offset is invalid
    */
    //---------------------------------------------------------------------
    public void insertString(int offset, String text, AttributeSet attributes)
        throws BadLocationException
    {
        // Knows about digits only
        char[] buf = text.toCharArray();
        for (int i=0; i < buf.length ; ++i)
        {
            String str = getText(0, getLength());
            int pos0 = str.indexOf('(');
            int pos1 = str.indexOf(") ") + 1;
            int pos2 = str.indexOf('-');

            if (pos1 < 1) pos1 = getLength();
            if (pos2 < 0) pos2 = getLength();

            if (offset > pos0 && offset <= pos1)
            {
                // Insert in NPA
                offset = insertToNpa(offset, buf[i], attributes);
            }
            else if (offset >= pos2 && getNxx().length() == 3)
            {
                // Insert to XXXX
                offset = insertToXxxx(offset, buf[i], attributes);
            }
            else if (offset > pos1 && offset <= pos2)
            {
                // Insert in NXX
                offset = insertToNxx(offset, buf[i], attributes);
            }
        }
    }

    //---------------------------------------------------------------------
    /**
       Inserts a digit to the NPA field.
       @param offset the position in the document to insert the digit
       @param digit the character digit to insert
       @param attributes the attributes for the text to insert
       @exception BadLocationException if the digit cannot be inserted
       at the given offset in the parent document
    */
    //---------------------------------------------------------------------
    protected int insertToNpa(int offset, char digit, AttributeSet attributes)
        throws BadLocationException
    {
        String inStr = (new Character(digit)).toString();
        if (digitAccepted(digit))
        {
            String old = getNpa();
            int val = 9;
            if(!isAlphaAccepted())
            {
                val = Integer.parseInt(inStr);
            }
            // Determine if the digit is valid
            if (!((old.length() == 0 || offset == 1) &&
                  (val == 0 || val == 1)) && old.length() < 4)
            {
                // The digit is valid.
                // Check for the parentheses.
                String text = getText(0, getLength());
                int pos0 = text.indexOf('(');
                int pos1 = text.indexOf(") ") + 1;
                if (pos1 == 0 && pos0 == -1)
                {
                    // Insert the ) then the ( and the digit.
                    super.insertString(0, ") ", attributes);
                    super.insertString(0, "(" + inStr, attributes);
                    offset += 2;
                    fireFirstDigit();
                }
                else if (pos1 == 0 && pos0 != -1)
                {
                    super.insertString(1, ") ", attributes);
                    super.insertString(1, inStr, attributes);
                    offset += 2;
                    fireFirstDigit();
                }
                else
                {
                    super.insertString(offset, inStr, attributes);
                    ++offset;
                }
                if (getNpa().length() == 3)
                {
                    fireNpaEntered();
                    offset += 2;
                }
            }
        }
        return offset;
    }

    //---------------------------------------------------------------------
    /**
       Inserts a digit to the Nxx field.
       @param offset the position in the document to insert the digit
       @param digit the character digit to insert
       @param attributes the attributes for the text to insert
       @exception BadLocationException if the digit cannot be inserted
       at the given offset in the parent document
    */
    //---------------------------------------------------------------------
    protected int insertToNxx(int offset, char digit, AttributeSet attributes)
        throws BadLocationException
    {
        String inStr = (new Character(digit)).toString();
        if (digitAccepted(digit))
        {
            String old = getNxx();
            // Determine if the digit is valid
            if (old.length() < 3)
            {
                super.insertString(offset, inStr, attributes);
                ++offset;
            }
        }
        return offset;
    }

    //---------------------------------------------------------------------
    /**
       Inserts a digit to the Xxxx field.
       @param offset the position in the document to insert the digit
       @param digit the character digit to insert
       @param attributes the attributes for the text to insert
       @exception BadLocationException if the digit cannot be inserted
       at the given offset in the parent document
    */
    //---------------------------------------------------------------------
    protected int insertToXxxx(int offset, char digit, AttributeSet attributes)
        throws BadLocationException
    {
        String inStr = (new Character(digit)).toString();
        if (digitAccepted(digit))
        {
            String old = getXxxx();
            // Determine if the digit is valid
            if (old.length() < 4)
            {
                if (getText(0, getLength()).indexOf('-') == -1)
                {
                    super.insertString(offset, "-", attributes);
                    ++offset;
                }
                super.insertString(offset, inStr, attributes);
                ++offset;
            }
        }
        return offset;
    }

    //---------------------------------------------------------------------
    /**
       Overrides the removal of characters.  It only blocks a '/' from being
       removed if there is something after it.
       @param offset the offset to start removing
       @param len the number of characters to remove
       @exception BadLocationException if the location is invalid
    */
    //---------------------------------------------------------------------
    public void remove(int offset, int len) throws BadLocationException
    {
        int limit = offset + len;

        if (offset == 0 && len == getLength())
        {
            super.remove(offset, len);
        }
        else
        {
            for (int i=limit-1; i >= offset; --i)
            {
                String text = getText(i, 1);
                int npa = getNpa().length();
                int nxx = getNxx().length();
                int xxxx = getXxxx().length();
                //need to remove the space affter ")"
                if ((text.charAt(0) == ')' || text.charAt(0) == '(') &&
                    (npa > 0 || nxx > 0 || xxxx > 0))
                {
                }
                else if (text.charAt(0) == '-' &&
                         getLength() > (i + 1))
                {
                }
                else
                {
                    super.remove(i, 1);
                    npa = getNpa().length();
                    nxx = getNxx().length();
                    xxxx = getXxxx().length();
                    if (nxx == 0 && xxxx == 0)
                    {
                        super.remove(0, getLength());
                    }
                }
            }
        }
    }

    //---------------------------------------------------------------------
    /**
       Remove a listener so it will not be notified when a field has been
       completed.
       @param l the listener to remove
    */
    //---------------------------------------------------------------------
    public void removeFieldListener(ActionListener l)
    {
        listenerList.remove(ActionListener.class, l);
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
    }
    
    /**
     * Determine whether to accept the user input as part of the phone number
     * @since 7.0
     * @param digit user input
     * @return true if accepted, otherwise false
     */
    public boolean digitAccepted(char digit)
    {
        boolean accepted = false;
        if(isAlphaAccepted())
        {
            accepted = Character.isLetterOrDigit(digit);
        }
        else
        {
            accepted = Character.isDigit(digit);
        }
        return accepted;
        
    }
    
    /**
     * Determine whether to accept letters in the phone number
     * @since 7.0
     * @return Returns the alphaAccepted.
     */
    public boolean isAlphaAccepted()
    {
        return alphaAccepted;
    }
    
    /**
     * Determine whether to accept letters in the phone number
     * @since 7.0
     * @param alphaAccepted The alphaAccepted to set.
     */
    public void setAlphaAccepted(boolean alphaAccepted)
    {
        this.alphaAccepted = alphaAccepted;
    }
}
