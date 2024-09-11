/* ===========================================================================

* Copyright (c) 2003, 2013, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/GovernmentIdDocument.java /main/15 2013/10/15 14:16:21 asinton Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    asinton   10/10/13 - removed references to social security number and
 *                         replaced with locale agnostic government id
 *    cgreene   05/28/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 * 4    360Commerce 1.3         1/25/2006 4:11:47 PM   Brett J. Larsen merge
 *      7.1.1 changes (aka. 7.0.3 fixes) into 360Commerce view
 * 3    360Commerce 1.2         3/31/2005 4:30:06 PM   Robert Pearse   
 * 2    360Commerce 1.1         3/10/2005 10:25:23 AM  Robert Pearse   
 * 1    360Commerce 1.0         2/11/2005 12:14:18 PM  Robert Pearse   
 *:
 * 6    .v700     1.2.1.2     1/6/2006 18:18:27      Deepanshu       CR 6113:
 *      Rolled back changes of CR 6113 for CR 8269
 * 5    .v700     1.2.1.1     11/16/2005 05:49:43    Akhilashwar K. Gupta
 *      Updated Code as per reviewer comment.
 * 4    .v700     1.2.1.0     11/9/2005 13:12:10     Deepanshu       CR 6113:
 *      Mask the SSN to display in XXX-XX-XXXX format
 * 3    360Commerce1.2         3/31/2005 15:30:06     Robert Pearse
 * 2    360Commerce1.1         3/10/2005 10:25:23     Robert Pearse
 * 1    360Commerce1.0         2/11/2005 12:14:18     Robert Pearse
 *
 *Revision 1.5  2004/09/23 00:07:11  kmcbride
 *@scr 7211: Adding static serialVersionUIDs to all POS Serializable objects, minus the JComponents
 *
 *Revision 1.4  2004/07/19 16:41:04  nrao
 *@scr 6221 Added checks to see if hyphens were already
 *present. If so, did not add the additional hyphens.
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
 *    Rev 1.2   Nov 21 2003 16:46:30   nrao
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

/**
 * This document controls a text field to ensure that input can only be
 * a North American phone number, (NPA)NXX-XXXX.  This class replaces Social
 * Security Document.
 * @since 14.0
 */
class GovernmentIdDocument extends PlainDocument
{
    // This id is used to tell
    // the compiler not to generate a
    // new serialVersionUID.
    //
    static final long serialVersionUID = -6179222882434823955L;

    /** list of objects interested in this class */
    protected EventListenerList listenerList;
    /** Constant for the SSN Part 1 */
    public static final int PART1 = 0;
    /** Constant for the SSN Part 2 */
    public static final int PART2 = 1;
    /** Constant for the SSN Part 3 */
    public static final int PART3 = 2;

    /**
     * Constructor.
     */
    public GovernmentIdDocument()
    {
        super();
        listenerList = new EventListenerList();
    }

    /**
     * Add a listener to be notified when a field has been completed.
     * @param l the listener to add
     */
    public void addFieldListener(ActionListener l)
    {
        listenerList.add(ActionListener.class, l);
    }

    /**
     * Remove a listener so it will not be notified when a field has been
     * completed.
     * @param l the listener to remove
     */
    public void removeFieldListener(ActionListener l)
    {
        listenerList.remove(ActionListener.class, l);
    }

    /**
     * Moves the caret past first hyphen.
     */
    protected void fireFirstPart() throws BadLocationException
    {
        fireFieldEvent(PART1);
    }

    /**
     * Moves the caret past Second hyphen.
     */
    protected void fireSecondPart() throws BadLocationException
    {
        fireFieldEvent(PART2);
    }

    /**
     * Notify all listeners that have registered interest for
     * notification on this event type.
     * @param field the field that was completed
     */
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

    /**
     * Gets the location of the first Hyphen
     * @return location of the first Hyphen -1 if not found
     */
    protected int getLocHyphen1()
    {
        int loc;
        try
        {
            loc = getText(0, getLength()).indexOf('-');
        }
        catch (BadLocationException excp)
        {
            loc = -1;
        }
        return loc;
    }

    /**
     * Gets the location of the Second Hyphen
     * @return location of the Second Hyphen -1 if not found
     */
    protected int getLocHyphen2()
    {
        int loc;
        try
        {
            int length = getLength();
            String text = getText(0, length);
            int firstLoc = getLocHyphen1();

            loc = text.substring(firstLoc + 1, length).indexOf('-');

            if(loc != -1)
                loc += (firstLoc + 1);
        }
        catch (BadLocationException excp)
        {
            loc = -1;
        }
        return loc;
    }

    /**
     * Gets the first 3 digits of the Government ID number
     * @return first 3 digits of the Government ID number
     */
    public String getPart1()
    {
        String text;
        int end = getLocHyphen1();
        if (end == -1)
        {
            end = getLength();
        }
        try
        {
            text = getText(0, getLength()).substring(0, end);
        }
        catch (BadLocationException excp)
        {
            text = "";
        }
        return text;
    }

    /**
     * Gets the middle 2 digits of the Government ID number
     * @return middle 2 digits of the Government ID number
     */
    public String getPart2()
    {
        String text = "";
        int start = getLocHyphen1();

        if (start != 1)
        {
            int end = getLocHyphen2();
            if (end == -1)
            {
                end = getLength();
            }

            try
            {
                text = getText(0, getLength()).substring(start + 1, end);
            }
            catch (BadLocationException excp)
            {
            }

        }
        return text;
    }

    /**
     * Gets the last 3 digits of the Government ID number
     * @return last 3 digits of the Government ID number
     */
    public String getPart3()
    {
        String text = "";
        int start = getLocHyphen1();

        if (start != -1)
        {
            start = getLocHyphen2();
            if (start != -1)
            {
                try
                {
                    text = getText(0, getLength()).substring(start + 1, getLength());
                }
                catch (BadLocationException excp)
                {
                }
            }
        }
        return text;
    }

    /**
     * Gets the format for the Government ID.
     * @return a pattern string for the Government ID
     */
    public String getFormat()
    {
        return "000-00-0000";
    }

    /**
     * Determines if the text can be inserted.
     * <b>Pre-conditions</b>
     * <ul>
     * <li> </li>
     * </ul>
     * <b>Post-conditions</b>
     * <ul>
     * <li> </li>
     * </ul>
     * @param offset the offset at which the text should be inserted
     * @param text the text to be inserted
     * @param attributes the set of attributes for the text
     * @exception BadLocationException if the offset is invalid
     */
    public void insertString(int offset, String text, AttributeSet attributes)
        throws BadLocationException
    {
        // Knows about digits only
        char[] buf = text.toCharArray();
        for (int i=0; i < buf.length ; ++i)
        {
            if(Character.isDigit(buf[i]))
            {
              String str = getText(0, getLength());
              int pos0 = getLocHyphen1();
              int pos1 = getLocHyphen2();
              if (pos0 < 0) pos0 = getLength();
              if (pos1 < 0) pos1 = getLength();
              if (offset <= pos0)
              {
                  // Insert into first part
                  offset = insertToPart1(offset, buf[i], attributes);
              }
              else if (offset > pos0 && offset <= pos1)
              {
                  // Insert into second Part
                  offset = insertToPart2(offset, buf[i], attributes);
              }
              else if (offset > pos0 && offset >= pos1)
              {
                  // Insert into third part
                  offset = insertToPart3(offset, buf[i], attributes);
              }
            }
            else
              return;
        }
    }

    /**
     * Inserts a digit into First Part of Government ID Number.
     * @param offset the position in the document to insert the digit
     * @param digit the character digit to insert
     * @param attributes the attributes for the text to insert
     * @exception BadLocationException if the digit cannot be inserted
     * at the given offset in the parent document
     */
    protected int insertToPart1(int offset, char digit, AttributeSet attributes)
        throws BadLocationException
    {
        if (getPart1().length() < 3)
        {
            super.insertString(offset, (new Character(digit)).toString(), attributes);
            ++offset;
            // only insert hyphen if it is not already present
            if (getPart1().length() == 3 && getLocHyphen1()!= 3)
            {
                super.insertString(3, "-", attributes);
                fireFirstPart();
                ++offset;
            }
        }
        return offset;
    }

    /**
     * Inserts a digit into Second Part of Government ID Number.
     * @param offset the position in the document to insert the digit
     * @param digit the character digit to insert
     * @param attributes the attributes for the text to insert
     * @exception BadLocationException if the digit cannot be inserted
     * at the given offset in the parent document
     */
    protected int insertToPart2(int offset, char digit, AttributeSet attributes)
        throws BadLocationException
    {
        if (getPart2().length() < 2)
        {
            super.insertString(offset, (new Character(digit)).toString(), attributes);
            ++offset;
            // only insert hyphen if it is not already present
            if (getPart2().length() == 2 && getLocHyphen2()!= 6)
            {
                super.insertString(6, "-", attributes);
                fireSecondPart();
                ++offset;
            }
        }
        return offset;
    }

    /**
     * Inserts a digit into Third Part of Government ID Number.
     * @param offset the position in the document to insert the digit
     * @param digit the character digit to insert
     * @param attributes the attributes for the text to insert
     * @exception BadLocationException if the digit cannot be inserted
     * at the given offset in the parent document
     */
    protected int insertToPart3(int offset, char digit, AttributeSet attributes)
        throws BadLocationException
    {
        if (getPart3().length() < 4)
        {
            super.insertString(offset, (new Character(digit)).toString(), attributes);
            ++offset;
        }
        return offset;
    }

}
