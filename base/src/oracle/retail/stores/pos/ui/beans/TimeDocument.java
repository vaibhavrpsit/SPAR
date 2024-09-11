/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/TimeDocument.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:58 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *   3    360Commerce 1.2         3/31/2005 4:30:31 PM   Robert Pearse   
 *   2    360Commerce 1.1         3/10/2005 10:26:16 AM  Robert Pearse   
 *   1    360Commerce 1.0         2/11/2005 12:15:08 PM  Robert Pearse   
 *
 *  Revision 1.4  2004/09/23 00:07:11  kmcbride
 *  @scr 7211: Adding static serialVersionUIDs to all POS Serializable objects, minus the JComponents
 *
 *  Revision 1.3  2004/03/16 17:15:18  build
 *  Forcing head revision
 *
 *  Revision 1.2  2004/02/11 20:56:26  rhafernik
 *  @scr 0 Log4J conversion and code cleanup
 *
 *  Revision 1.1.1.1  2004/02/11 01:04:23  cschellenger
 *  updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 16:12:50   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.1   Aug 07 2002 19:34:26   baa
 * remove hard coded date formats
 * Resolution for POS SCR-1740: Code base Conversions
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
import javax.swing.text.SimpleAttributeSet;

import org.apache.log4j.Logger;

// Foundation imports

//-------------------------------------------------------------------------
/**
   This document controls a text field to ensure that input can only be
   a time.

   @version $KW=@(#); $Ver=pos_4.5.0:50; $EKW;
*/
//-------------------------------------------------------------------------
class TimeDocument extends PlainDocument
{
    // This id is used to tell
    // the compiler not to generate a
    // new serialVersionUID.
    //
    static final long serialVersionUID = -2880253011000130352L;

    /** 
        The logger to which log messages will be sent.
    **/
    protected static Logger logger = Logger.getLogger(oracle.retail.stores.pos.ui.beans.TimeDocument.class);

    /** revision number supplied by Team Connection */
    public static final String revisionNumber = "$KW=@(#); $Ver=pos_4.5.0:50; $EKW;";
    /** the code for minute */
    public static final String MINUTE = "mm";
    /** the code for hour */
    public static final String HOUR   = "HH";
    /** the value for an empty field */
    protected static final int EMPTY = -1;
    /** the delimeter character */
    protected String delim  = ":";
    /** the field number for the minute */
    protected int minute;
    /** the field number for the hour */
    protected int hour;
    /**
       An array that holds the format for each field number.
       To set the fields and their order, set the variables hour, minute,
       and then set fields[minute] to MINUTE, etc.
    */
    protected String[] fields = null;
    /** listeners interested in receiving events from this object */
    protected EventListenerList listenerList;

    //---------------------------------------------------------------------
    /**
       A structure to hold the substring positions of one field.
    */
    //---------------------------------------------------------------------
    protected class Segment
    {
        public int start;
        public int end;
    }

    //---------------------------------------------------------------------
    /**
       Constructor. Constructs date in international format.
    */
    //---------------------------------------------------------------------
    public TimeDocument()
    {
        this(HOUR, MINUTE);
    }

    //---------------------------------------------------------------------
    /**
       Constructor that lets the user specify the format.  There should be
       no more than one field with a day, month, or year.
       @param field1 the first field.  This should be one of
       <ul>
       <li><code>HOUR</code></li>
       <li><code>MINUTE</code></li>
       </ul>
       @param field2 same as field1
    */
    //---------------------------------------------------------------------
    public TimeDocument(String field1, String field2)
    {
        setFormat(field1, field2);
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
       Moves the caret past the semicolon.
    */
    //---------------------------------------------------------------------
    protected void fireHourEntered() throws BadLocationException
    {
        // Find where the hour field ends.
        Segment seg = getSegment(hour);
        // determine if there is a field after the hour.
        if (minute > hour)
        {
            if (getLength() > seg.end)
            {
                // If so and there is a delimiter already, move the caret
                // by firing to the field.
                fireFieldEvent(hour);
            }
            else
            {
                // If so and there is no delimiter, add the delimeter
                super.insertString(seg.end, delim, SimpleAttributeSet.EMPTY);
            }
        }
    }

    //---------------------------------------------------------------------
    /**
       Notify all listeners that have registered interest for
       notification on this event type.  The event instance
       is lazily created using the parameters passed into
       the fire method.
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
       Moves the caret past the colon if there is a field after this.
    */
    //---------------------------------------------------------------------
    protected void fireMinuteEntered() throws BadLocationException
    {
        // Find where the minute field ends.
        Segment seg = getSegment(minute);
        // determine if there is a field after the minute
        if (hour > minute)
        {
            if (getLength() > seg.end)
            {
                // If so and there is a delimiter already, move the caret
                // by firing to the field.
                fireFieldEvent(minute);
            }
            else
            {
                // If so and there is no delimiter, add the delimeter
                super.insertString(seg.end, delim, SimpleAttributeSet.EMPTY);
            }
        }
        // do nothing if the minute is the last field
    }

    //---------------------------------------------------------------------
    /**
       Gets the hours in a day.
       @return the hours in a day.
    */
    //---------------------------------------------------------------------
    public int getHoursInDay()
    {
        return 24;
    }

    //---------------------------------------------------------------------
    /**
       Gets the hour.
       @return the hour value from 0 to 23, or -1 if it is not entered
    */
    //---------------------------------------------------------------------
    public int getHour()
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
        int h = -1;
        Segment seg = getSegment(hour);
        if (seg.end > seg.start)
        {
            h = Integer.parseInt(text.substring(seg.start, seg.end));
        }
        return h;
    }

    //---------------------------------------------------------------------
    /**
       Gets the format for the date.
       @return a pattern string for the current date
    */
    //---------------------------------------------------------------------
    public String getFormat()
    {
        StringBuffer format = new StringBuffer();
        int limit = Math.max(hour, minute);
        for (int i=0; i <= limit; ++i)
        {
            if (format.length() > 0)
            {
                format.append(delim);
            }
            format.append(fields[i]);
        }
        return format.toString();
    }

    //---------------------------------------------------------------------
    /**
       Gets the number of minutes in an hour.
       @return the number of minutes in an hour
    */
    //---------------------------------------------------------------------
    public int getMinutesInHour()
    {
        return 60;
    }

    //---------------------------------------------------------------------
    /**
       Gets the minutes.
       @return the minutes value from 0 to 59, or -1 if it is not entered
    */
    //---------------------------------------------------------------------
    public int getMinutes()
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
        int m = -1;
        Segment seg = getSegment(minute);
        if (seg.end > seg.start)
        {
            m = Integer.parseInt(text.substring(seg.start, seg.end));
        }
        return m;
    }

    //---------------------------------------------------------------------
    /**
       Gets the offsets for the segment.
       @param field the field
       @return the segment for the given field
    */
    //---------------------------------------------------------------------
    protected Segment getSegment(int field)
    {
        Segment rv = new Segment();
        String text;
        try
        {
            text = getText(0, getLength());
        }
        catch (BadLocationException excp)
        {
            text = "";
        }

        rv.start  = rv.end = 0;

        if (field != EMPTY)
        {   for (int i=0; i < field && rv.start != -1 ; ++i)
        {
            rv.start = text.indexOf(delim, rv.start + 1);
        }
        if( rv.start == -1) // deliter not found
        {
            rv.end = rv.start;
        }
        else //find end point
        {
            //check for next delim
            if(field > 0) //we're not the first field
            {
                rv.start += 1; //step past delimiter
            }
            rv.end = text.indexOf(delim, rv.start);
            if (rv.end == -1)  //we're at the end
            {
                rv.end = text.length();
            }
        }
        }
        return rv;
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
        // Knows about digits and ':' char
        char[] buf = text.toCharArray();
        for (int i=0; i < buf.length ; ++i)
        {
            // Figure out where the input is being done.
            int max = Math.max(hour, minute);
            for (int j=0; j <= max ; ++j)
            {
                Segment seg = getSegment(j);
                if (offset + i <= seg.end)
                {
                    insertToField(j, offset + i, buf[i], seg.start,
                                  seg.end, attributes);
                    j = max + 2;
                }
            }
        }
    }

    //---------------------------------------------------------------------
    /**
       Inserts to the appropriate field.
       @param offset the position in the document to insert the digit
       @param digit the character digit to insert
       @param start the start position of this field
       @param end the position after this field
       @param attributes the attributes for the text to insert
       @exception BadLocationException if the digit cannot be inserted
       at the given offset in the parent document
    */
    //---------------------------------------------------------------------
    protected void insertToField(int fieldNo, int offset, char digit,
                                 int start,   int end,    AttributeSet atts)
        throws BadLocationException
    {
        if (fieldNo == minute)
        {
            insertToMinute(offset, digit, start, end, atts);
        }
        else if (fieldNo == hour)
        {
            insertToHour(offset, digit, start, end, atts);
        }
    }

    //---------------------------------------------------------------------
    /**
       Inserts a digit to the minute field if the digit is valid for the
       position.
       @param offset the position in the document to insert the digit
       @param digit the character digit to insert
       @param start the start position of this field
       @param end the position after this field
       @param attributes the attributes for the text to insert
       @exception BadLocationException if the digit cannot be inserted
       at the given offset in the parent document
    */
    //---------------------------------------------------------------------
    protected void insertToMinute(int offset, char digit, int start, int end,
                                  AttributeSet attributes)
        throws BadLocationException
    {
        String inStr = (new Character(digit)).toString();
        if (Character.isDigit(digit))
        {
            if (end - start < fields[minute].length())
            {
                int val = Integer.parseInt(inStr);
                if (start != end)
                {
                    // Get the digits in the minute.
                    String text = getText(start, end - start);
                    int oldVal = Integer.parseInt(text);
                    if (offset == start)
                    {
                        int m = 10 * val + oldVal;
                        if (m <= getMinutesInHour() && m >= 0)
                        {
                            super.insertString(offset, inStr, attributes);
                            fireMinuteEntered();
                        }
                    }
                    else if (offset == start + 1)
                    {
                        // Insert at end
                        int m = 10 * oldVal + val;
                        if (m <= getMinutesInHour() && m >= 0)
                        {
                            super.insertString(offset, inStr, attributes);
                            fireMinuteEntered();
                        }
                    }
                }
                else
                {
                    // NOTE: This should check the minute.
                    // This is the only digit, so anything is valid.
                    super.insertString(offset, inStr, attributes);
                    if (val > (getMinutesInHour() / 10))
                    {
                        fireMinuteEntered();
                    }
                }
            }
        }
        else if (digit == delim.charAt(0) && getMinutes() > -1)
        {
            fireMinuteEntered();
        }
    }

    //---------------------------------------------------------------------
    /**
       Inserts a digit to the hour field if the digit is valid for the
       position.
       @param offset the position in the document to insert the digit
       @param digit the character digit to insert
       @param start the start position of this field
       @param end the position after this field
       @param attributes the attributes for the text to insert
       @exception BadLocationException if the digit cannot be inserted
       at the given offset in the parent document
    */
    //---------------------------------------------------------------------
    protected void insertToHour(int offset, char digit, int start, int end,
                                AttributeSet attributes)
        throws BadLocationException
    {
        String inStr = (new Character(digit)).toString();
        if (Character.isDigit(digit))
        {
            if (end - start < fields[hour].length())
            {
                int val = Integer.parseInt(inStr);
                if (start != end)
                {
                    // Get the digits in the month.
                    String text = getText(start, end - start);
                    int oldVal = Integer.parseInt(text);
                    if (offset == start)
                    {
                        int h = val * 10 + oldVal;
                        if (h < getHoursInDay() && h >= 0)
                        {
                            super.insertString(offset, inStr, attributes);
                            fireHourEntered();
                        }
                    }
                    else if (offset == start + 1)
                    {
                        int h = oldVal * 10 + val;
                        if (h < getHoursInDay() && h >= 0)
                        {
                            super.insertString(offset, inStr, attributes);
                            fireHourEntered();
                        }
                    }
                }
                else
                {
                    // This is the only digit, so anything is valid.
                    super.insertString(offset, inStr, attributes);
                    if (val > (getHoursInDay() / 10))
                    {
                        fireHourEntered();
                    }
                }
            }
        }
        else if (digit == delim.charAt(0) && getHour() > -1)
        {
            fireHourEntered();
        }
    }


    //---------------------------------------------------------------------
    /**
       Finds out if the hour is in the format.
       @return true if the hour is in the format, false otherwise
    */
    //---------------------------------------------------------------------
    public boolean isHourInFormat()
    {
        return(isInFormat(HOUR, hour));
    }

    //---------------------------------------------------------------------
    /**
       Finds out if the minute is in the format.
       @return true if the minute is in the format, false otherwise
    */
    //---------------------------------------------------------------------
    public boolean isMinuteInFormat()
    {
        return(isInFormat(MINUTE, minute));
    }


    //---------------------------------------------------------------------
    // Private function to encapsulate exception catching
    //---------------------------------------------------------------------
    protected boolean isInFormat(String formatStr, int index)
    {
        boolean isFormat;
        try
        {
            isFormat = formatStr.equals(fields[index]);
        }
        catch( ArrayIndexOutOfBoundsException e )
        {
            isFormat = false;
        }
        return( isFormat );
    }

    //---------------------------------------------------------------------
    /**
       Overrides the removal of characters.  It only blocks a ':' from being
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
                if (!(text.charAt(0) == delim.charAt(0) &&
                      getLength() > (i + 1)))
                {
                    super.remove(i, 1);
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

    //---------------------------------------------------------------------
    /**
       Sets the format to be used for the class. This method clears the
       field.
       @param field1 the first field.  This should be one of
       <ul>
       <li><code>HOUR</code></li>
       <li><code>MINUTE</code></li>
       </ul>
       @param field2 same as field1
    */
    //---------------------------------------------------------------------
    public void setFormat(String field1, String field2)
    {
        try
        {
            super.remove(0, getLength());
        }
        catch (BadLocationException excp)
        {
        }
        fields = new String[2];
        fields[0] = field1;
        fields[1] = field2;
        hour = -1;
        minute = -1;
        for (int i=0; i < 2; ++i)
        {
            if (HOUR.equals(fields[i]))
            {
                hour = i;
            }
            else if (MINUTE.equals(fields[i]))
            {
                minute = i;
            }
            else
            {
                //should log just in case
                logger.error( 
                             "Unknown field: " + Integer.toString(i) + " format: " + fields[i] + "");
            }
        }
        listenerList = new EventListenerList();
    }
}
