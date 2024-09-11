/* ===========================================================================
* Copyright (c) 1998, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/DateDocument.java /main/16 2012/03/09 16:38:51 mjwallac Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    mjwallac  03/09/12 - Fortify: synchronize calls to parse() and format()
 *                         methods in java.text.Format class to fix race
 *                         condition.
 *    cgreene   12/05/11 - updated from deprecated packages and used more
 *                         bigdecimal constants
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/26/10 - XbranchMerge cgreene_tech43 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    cgreene   04/02/10 - remove deprecated LocaleContantsIfc and currencies
 *    abondala  01/03/10 - update header date
 *    mkochumm  02/12/09 - use default locale for dates
 *
 * ===========================================================================
 * $Log:
 *   11   360Commerce 1.10        8/13/2007 1:29:55 PM   Mathews Kochummen
 *        update comment
 *   10   360Commerce 1.9         8/3/2007 5:22:00 PM    Mathews Kochummen
 *        handle separate year field
 *   9    360Commerce 1.8         7/25/2007 9:20:22 AM   Mathews Kochummen put
 *        back rev.7
 *   8    360Commerce 1.7         7/23/2007 3:54:53 PM   Mathews Kochummen use
 *        correct formatter
 *   7    360Commerce 1.6         7/18/2007 4:44:36 PM   Mathews Kochummen
 *        validate year
 *   6    360Commerce 1.5         5/21/2007 2:19:33 PM   Mathews Kochummen
 *        format credit card exp. date
 *   5    360Commerce 1.4         5/20/2007 7:32:48 PM   Mathews Kochummen fix
 *        format
 *   4    360Commerce 1.3         5/16/2007 3:33:01 PM   Mathews Kochummen use
 *        the locale's pattern
 *   3    360Commerce 1.2         3/31/2005 4:27:41 PM   Robert Pearse   
 *   2    360Commerce 1.1         3/10/2005 10:20:48 AM  Robert Pearse   
 *   1    360Commerce 1.0         2/11/2005 12:10:29 PM  Robert Pearse   
 *
 *  Revision 1.6  2004/09/23 00:07:11  kmcbride
 *  @scr 7211: Adding static serialVersionUIDs to all POS Serializable objects, minus the JComponents
 *
 *  Revision 1.5  2004/07/28 16:01:12  aachinfiev
 *  @scr 4345 - Disallowed entry of incorrect days & months: e.g. 0/0/2005
 *
 *  Revision 1.4  2004/03/16 17:15:22  build
 *  Forcing head revision
 *
 *  Revision 1.3  2004/03/16 17:15:17  build
 *  Forcing head revision
 *
 *  Revision 1.2  2004/02/11 20:56:27  rhafernik
 *  @scr 0 Log4J conversion and code cleanup
 *
 *  Revision 1.1.1.1  2004/02/11 01:04:21  cschellenger
 *  updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.1   Oct 22 2003 19:17:58   epd
 * made class public
 * removed unused local variable
 * 
 *    Rev 1.0   Aug 29 2003 16:10:04   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.2   Aug 14 2002 18:17:10   baa
 * format currency 
 * Resolution for POS SCR-1740: Code base Conversions
 * 
 *    Rev 1.1   Aug 07 2002 19:34:16   baa
 * remove hard coded date formats
 * Resolution for POS SCR-1740: Code base Conversions
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.beans;



import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.StringTokenizer;

import javax.swing.event.EventListenerList;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;
import javax.swing.text.SimpleAttributeSet;

import org.apache.log4j.Logger;

import oracle.retail.stores.commerceservices.common.datetime.DateTimeServiceIfc;
import oracle.retail.stores.commerceservices.common.datetime.DateTimeServiceLocator;
import oracle.retail.stores.domain.utility.LocaleConstantsIfc;
import oracle.retail.stores.common.utility.LocaleMap;

// Foundation imports

//-------------------------------------------------------------------------
/**
   This document controls a text field to ensure that input can only be
   a date.
   @version $Revision: /main/16 $
*/
//-------------------------------------------------------------------------
public class DateDocument extends PlainDocument
{
    // This id is used to tell
    // the compiler not to generate a
    // new serialVersionUID.
    //
    static final long serialVersionUID = 1104833214848784999L;

    /** 
        The logger to which log messages will be sent.
    **/
    protected static Logger logger = Logger.getLogger(oracle.retail.stores.pos.ui.beans.DateDocument.class);

    /** revision number supplied by Team Connection */
    public static final String revisionNumber = "$Revision: /main/16 $";
    /** the code for month */
    public static final String MONTH = "MM";
    /** the code for day */
    public static final String DAY   = "dd";
    /** the code for four-digit year */
    public static final String YEAR4 = "yyyy";
    /** the code for two-digit year */
    public static final String YEAR2 = "yy";
    /** the value for an empty field */
    protected static final int EMPTY = -1;
    /** the delimeter character */
    protected String delim  = "/";
    /** the field number for the month */
    protected int month;
    /** the field number for the day */
    protected int day;
    /** the field number for the year */
    protected int year;
    /** the style that indicates displaying only month and year on a datefield */
    public static final int MONTH_YEAR = 5;
    public static final int MONTH_DAY = 6;
    //format for credit card expiration date MM/YYYY
    public static final int CREDITCARD_MONTH_YEAR = 7;
    
    /**
       An array that holds the format for each field number.
       To set the fields and their order, set the variables month, day,
       and year, and then set fields[month] to MONTH, etc.
    */
    protected String[] fields;
    /** listeners interested in receiving events from this object */
    protected EventListenerList listenerList;

    /** date format used */
    protected SimpleDateFormat  dateFormat;
    /** the ui subsystem locale**/
    protected Locale locale;
    //year value when the year occurs in a ui field by itself and is not part of the day/month field
    protected int separateYear = -1;

    
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
    public DateDocument()
    {
        locale = LocaleMap.getLocale(LocaleConstantsIfc.DEFAULT_LOCALE);
        setFormat(DateFormat.SHORT);
    }


    //---------------------------------------------------------------------
    /**
       Constructor. Constructs date in international format.
    */
    //---------------------------------------------------------------------
    public DateDocument(int format,Locale locale)
    {
        this.locale = locale;
        setFormat(format);
    }

    //---------------------------------------------------------------------
    /**
       Constructor that lets the user specify the format.  There should be
       no more than one field with a day, month, or year.
       @param field1 the first field.  This should be one of
       <ul>
       <li><code>MONTH</code></li>
       <li><code>DAY</code></li>
       <li><code>YEAR2</code></li>
       <li><code>YEAR4</code></li>
       </ul>
       @param field2 same as field1
       @param field3 same as field1
       @deprecated as of release 5.5 replace by DateDocument(int format, Locale locale)
    */
    //---------------------------------------------------------------------
    public DateDocument(String field1, String field2, String field3)
    {
        locale = LocaleMap.getLocale(LocaleConstantsIfc.DEFAULT_LOCALE);
        setFormat(DateFormat.SHORT);
    }

    //---------------------------------------------------------------------
    /**
       Gets the document locale
       @returns the document's locale
    */
    //---------------------------------------------------------------------
    public Locale getLocale()
    {
        return locale;
    }
    
    //---------------------------------------------------------------------
    /**
       Sets the document locale
       @param locale  the new locale
    */
    //---------------------------------------------------------------------
    public void setLocale(Locale locale)
    {
        this.locale = locale;
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
       Moves the caret past the slash if there is a field after this.
    */
    //---------------------------------------------------------------------
    protected void fireDayEntered() throws BadLocationException
    {
        // Find where the month field ends.
        Segment seg = getSegment(day);
        // determine if there is a field after the day.
        if (year > day || month > day)
        {
            if (getLength() > seg.end)
            {
                // Move the caret
                // by firing to the field.
                fireFieldEvent(day);
            }
             else
            {
                // If so and there is no delimiter, add the delimeter
                super.insertString(seg.end, delim, SimpleAttributeSet.EMPTY);
            }
        }
        // do nothing if the day is the last field
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
       Moves the caret past the slash if there is a field after this.
    */
    //---------------------------------------------------------------------
    protected void fireMonthEntered() throws BadLocationException
    {
        // Find where the month field ends.
        Segment seg = getSegment(month);
        // determine if there is a field after the month
        if (year > month || day > month)
        {
            if (getLength() > seg.end)
            {
                // If so and there is a delimiter already, move the caret
                // by firing to the field.
                fireFieldEvent(month);
            }
            else
            {
                // If so and there is no delimiter, add the delimeter
                super.insertString(seg.end, delim, SimpleAttributeSet.EMPTY);
            }
        }
        // do nothing if the month is the last field
    }

    //---------------------------------------------------------------------
    /**
       Moves the caret past the slash if there is a field after this.
    */
    //---------------------------------------------------------------------
    protected void fireYearEntered() throws BadLocationException
    {
        // Find where the year field ends.
        Segment seg = getSegment(year);
        // determine if there is a field after the year
        if (day > year || month > year)
        {
            if (getLength() > seg.end)
            {
                // If so and there is a delimiter already, move the caret
                // by firing to the field.
                fireFieldEvent(year);
            }
            else
            {
                // If so and there is no delimiter, add the delimeter
                super.insertString(seg.end, delim, SimpleAttributeSet.EMPTY);
            }
        }
        // do nothing if the year is the last field
    }

    //---------------------------------------------------------------------
    /**
       Gets the days in the month.
       @return the days in the month.
    */
    //---------------------------------------------------------------------
    public int getDaysInMonth()
    {
        return 31;
    }

    //---------------------------------------------------------------------
    /**
       Gets the day
       @return the day, or -1 if it is not entered
    */
    //---------------------------------------------------------------------
    public int getDay()
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
        int dy = -1;
        Segment seg = getSegment(day);
        if (seg.end > seg.start)
        {
            dy = Integer.parseInt(text.substring(seg.start, seg.end));
        }
        return dy;
    }

    //---------------------------------------------------------------------
    /**
       Gets the short format for the date for the locale
       @return a pattern string for the current date
    */
    //---------------------------------------------------------------------
    public String getFormat()
    {
        DateTimeServiceIfc dateTimeService = DateTimeServiceLocator.getDateTimeService();
        SimpleDateFormat fmt = dateTimeService.getDateFormatter(getLocale(), DateFormat.SHORT);
        String pat = fmt.toPattern();
        return pat;
    }
    //---------------------------------------------------------------------
    /**
       Gets the localized short format for the date for the locale
       @return a pattern string for the current date using locale caracters
    */
    //---------------------------------------------------------------------
    public String getLocalizedFormat()
    {
        DateTimeServiceIfc dateTimeService = DateTimeServiceLocator.getDateTimeService();
        SimpleDateFormat fmt = dateTimeService.getDateFormatter(getLocale(), DateFormat.SHORT);
        String pat = fmt.toLocalizedPattern();
        return pat;
    }
     //---------------------------------------------------------------------
    /**
       Gets the format for the date for the current DateDocument instance
       @return a handle to the date format object
    */
    //---------------------------------------------------------------------
    public DateFormat getDateFormat()
    {
        return dateFormat;
    }
    //---------------------------------------------------------------------
    /**
       Gets the number of months in a year.
       @return the number of months in a year
    */
    //---------------------------------------------------------------------
    public int getMonthsInYear()
    {
        return 12;
    }

    //---------------------------------------------------------------------
    /**
       Gets the month.
       @return the month value , or -1 if it is not entered
    */
    //---------------------------------------------------------------------
    public int getMonth()
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
        int mo = -1;
        Segment seg = getSegment(month);
        if (seg.end > seg.start)
        {
            mo = Integer.parseInt(text.substring(seg.start, seg.end));
        }
        return mo;
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
        {  
            for (int i=0; i < field && rv.start != -1 ; ++i)
            {
               rv.start = text.indexOf(delim, rv.start + 1);
            }
            if( rv.start == -1) // delimiter not found
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
       Gets the year.
       @return the year value
    */
    //---------------------------------------------------------------------
    public int getYear()
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
        int yr = -1;
        Segment seg = getSegment(year);
        if (year != -1 && fields[year] !=null && fields[year].equals(YEAR2) && ((seg.end-seg.start) < 2))
        {
        	return yr;
        }
        if (seg.end > seg.start)
        {
            yr = Integer.parseInt(text.substring(seg.start, seg.end));
            if (fields[year]==YEAR2)
            {
                if (yr < 50)
                {
                    yr += 2000;
                }
                else
                {
                    yr += 1900;
                }
            }
        }
        return yr;
    }

    //---------------------------------------------------------------------
    /**
       Gets the year.
       @return true if year is 4 digit format
    */
    //---------------------------------------------------------------------
    public boolean isYear4Digit()
    {
        return isInFormat(YEAR4, year);
    }

    //---------------------------------------------------------------------
    /**
       Gets the year.
       @return true if year is 2 digit format
    */
    //---------------------------------------------------------------------
    public boolean isYear2Digit()
    {
        return isInFormat(YEAR2, year);
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
      // Knows about digits and '/' chars.
        char[] buf = text.toCharArray();
        for (int i=0; i < buf.length ; ++i)
        {
            // Figure out where the input is being done.
            int max = Math.max(day, Math.max(month, year));
            for (int j=0; j <= max ; ++j)
            {
                Segment seg = getSegment(j);
                if ( (offset + i <= seg.end))
                {
                    insertToField(j, offset + i, buf[i], seg.start,
                                  seg.end, attributes);
                    j = max +2;
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
        // make sure that the current field is part of the mask
    
        if (fieldNo == month)
        {
           insertToMonth(offset, digit, start, end, atts);
        }
        else if (fieldNo == day)
        {
           insertToDay(offset, digit, start, end, atts);
        }
        else if (fieldNo == year)
        {
           insertToYear(offset, digit, start, end, atts);
        }
   }

    //---------------------------------------------------------------------
    /**
       Inserts a digit to the day field if the digit is valid for the
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
    protected void insertToDay(int offset, char digit, int start, int end,
                               AttributeSet attributes)
        throws BadLocationException
    {

            String inStr = (new Character(digit)).toString();
            if (Character.isDigit(digit))
            {
                if (end - start < fields[day].length())
                {
                    int val = Integer.parseInt(inStr);
                    if (start != end)
                    {
                        // Get the digits in the day.
                        String text = getText(start, end - start);
                        int oldVal = Integer.parseInt(text);
                        if (offset == start)
                        {
                            // Insert at beginning.
                            int dy = 10 * val + oldVal;
                            if (dy <= getDaysInMonth() && dy > 0)
                            {
                                super.insertString(offset, inStr, attributes);
                                fireDayEntered();
                            }
                        }
                        else if (offset == start + 1)
                        {
                            // Insert at end of month
                            int dy = 10 * oldVal + val;
                            if (dy <= getDaysInMonth() && dy > 0)
                            {
                                super.insertString(offset, inStr, attributes);
                                fireDayEntered();
                            }
                        }
                    }
                    else
                    {
                        // NOTE: This should check the month.
                        // This is the only digit, so anything is valid.
                        super.insertString(offset, inStr, attributes);
                        if (val > (getDaysInMonth() / 10))
                        {
                            fireDayEntered();
                        }
                    }
                }
            }
            else if (digit == delim.charAt(0) && getDay() > 0)
            {
                fireDayEntered();
            }
     
    }

    //---------------------------------------------------------------------
    /**
       Inserts a digit to the month field if the digit is valid for the
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
    protected void insertToMonth(int offset, char digit, int start, int end,
                                 AttributeSet attributes)
        throws BadLocationException
    {
       // Make sure to move on if the month is not part of the mask

            String inStr = (new Character(digit)).toString();
            if (Character.isDigit(digit))
            {
                if (end - start < fields[month].length())
                {
                    int val = Integer.parseInt(inStr);
                    if (start != end)
                    {
                        // Get the digits in the month.
                        String text = getText(start, end - start);
                        int oldVal = Integer.parseInt(text);
                        if (offset == start)
                        {
                            int mo = val * 10 + oldVal;
                            if (mo <= getMonthsInYear() && mo > 0)
                            {
                                super.insertString(offset, inStr, attributes);
                                fireMonthEntered();
                            }
                        }
                        else if (offset == start + 1)
                        {
                            int mo = oldVal * 10 + val;
                            if (mo <= getMonthsInYear() && mo > 0)
                            {
                                super.insertString(offset, inStr, attributes);
                                fireMonthEntered();
                            }
                        }
                    }
                    else
                    {
                        // This is the only digit, so anything is valid.
                        super.insertString(offset, inStr, attributes);
                        if (val > (getMonthsInYear() / 10))
                        {
                            fireMonthEntered();
                        }
                    }
                }
            }
            else if (digit == delim.charAt(0) && getMonth() > 0)
            {
                fireMonthEntered();
            }
    
    }

    //---------------------------------------------------------------------
    /**
       Inserts a digit to the year field if the digit is valid for the
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
    protected void insertToYear(int offset, char digit, int start, int end,
                                AttributeSet attributes)
        throws BadLocationException
    {

            String inStr = (new Character(digit)).toString();
            if (Character.isDigit(digit))
            {
                // Get the digits in the year.
                String text = getText(start, end - start);
                if (text.length() < fields[year].length())
                {
                    super.insertString(offset, inStr, attributes);
                }
                if (text.length() == fields[year].length()-1)
                {
                    fireYearEntered();
                }
            }
            else if (digit == delim.charAt(0) && getYear() > -1)
            {
                fireYearEntered();
            }
       
    }


    //---------------------------------------------------------------------
    /**
       Finds out if the day is in the format.
       @return true if the day is in the format, false otherwise
    */
    //---------------------------------------------------------------------
    public boolean isDayInFormat()
    {
        return(isInFormat(DAY, day));
    }

    //---------------------------------------------------------------------
    /**
       Finds out if the month is in the format.
       @return true if the month is in the format, false otherwise
    */
    //---------------------------------------------------------------------
    public boolean isMonthInFormat()
    {
        return(isInFormat(MONTH, month));
    }

    //---------------------------------------------------------------------
    /**
       Finds out if the year is in the format.
       @return true if the year is in the format, false otherwise
    */
    //---------------------------------------------------------------------
    public boolean isYearInFormat()
    {
        return (isInFormat(YEAR4, year) || isInFormat(YEAR2, year));
    }


    //---------------------------------------------------------------------
    //  function to encapsulate exception catching
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
       <li><code>MONTH</code></li>
       <li><code>DAY</code></li>
       <li><code>YEAR2</code></li>
       <li><code>YEAR4</code></li>
       </ul>
       @param field2 same as field1
       @param field3 same as field1
       @deprecated as of release 5.5 replace by setFormat(int style);
    */
    //---------------------------------------------------------------------
    public void setFormat(String field1, String field2, String field3)
    {
        setFormat(DateFormat.SHORT);
    }
    //---------------------------------------------------------------------
    /**
       Sets the format to be used for the class. This method clears the
       field.
       @param field1 the first field.  This should be one of
       <ul>
       <li><code>MONTH</code></li>
       <li><code>DAY</code></li>
       <li><code>YEAR2</code></li>
       <li><code>YEAR4</code></li>
       </ul>
       @param field2 same as field1
       @param field3 same as field1
    */
    //---------------------------------------------------------------------
    public void setFormat(int style)
    {
        day = month = year = -1; 
        try
        {
            super.remove(0, getLength());
        }
        catch (BadLocationException excp)
        {
        }
        // to dertermine were is the month, year, date located  use the date format pattern
        if (style == MONTH_YEAR || style == MONTH_DAY)
        {
            dateFormat = (SimpleDateFormat) DateFormat.getDateInstance(DateFormat.SHORT,locale); 
            fields = new String[2];
        }
        else if (style == CREDITCARD_MONTH_YEAR)
        {
        	dateFormat = new SimpleDateFormat("MM/yyyy", locale);
        	fields = new String[2];
        }
        else
        { 
            dateFormat = (SimpleDateFormat) DateFormat.getDateInstance(style,locale); 
            fields = new String[3];
        }
        
        String pattern = dateFormat.toPattern();   // as long as we use the standard pattern and not the localized
                                                   // it is fine to check for M, d and y characters
                                                   // regardless locale. 
        StringTokenizer parts = new StringTokenizer(pattern,getDelimiter(pattern));
        int indx=0;
        // fix format pattern as necessary
        while (parts.hasMoreTokens())
        {
           String token = parts.nextToken();
           if (token.endsWith("M"))
           {
              month = indx;
              fields[indx]=MONTH;
              indx++;
           }
           else if (token.endsWith("d"))
           {
              if (style != MONTH_YEAR && style != CREDITCARD_MONTH_YEAR)
              {
                 day = indx;

                  fields[indx]=DAY;
                  indx++;
               }
            }
           else if (token.endsWith("y"))
           {
               if (style != MONTH_DAY)
               {
                 year =indx;
                 if (style == MONTH_YEAR)
                 {
                	 //for places like credit card expiration dates that take a 4 digit year
                	 fields[indx]=YEAR4;
                 }
                 else
                 {
                	 fields[indx]=token;
                 }
                 indx++;
               }
           }
           else
           {
              //should log just in case
                logger.error( 
                             "Unknown field: " + Integer.toString(indx) + " format: " + token + "");
           }
       }
       // fix the mask according to the valid fields
       // to remove either day or year if necessary
       pattern = "";
       for ( int i =0; i < fields.length; i++)
       {
              // add the delimiter to the pattern;
              if (i >0)
              {
                 pattern= pattern +delim;
              }
              pattern= pattern + fields[i];
        }
        
        // apply the new mask
        dateFormat.applyPattern(pattern);
        listenerList = new EventListenerList();
    }

    //---------------------------------------------------------------------
    /**
       Gets the date fields delimiter given a localized pattern.
       @return the parsed date
    */
    //---------------------------------------------------------------------
    public String getDelimiter(String pattern)
    {
        //get delimiter for the current locale
        for (int i=0; i < pattern.length(); i++)
        {
            if (!Character.isLetter(pattern.charAt(i)))
            {
               delim = new Character(pattern.charAt(i)).toString();
               break;
            }
        }
        return delim;
    }

    /**
     * gets the year  value when the year occurs in a ui field by itself and is not part of the day/month field
     * @return separate year value
     */
    protected int getSeparateYear()
    {
   	    return separateYear;
    }
    
    /**
     * sets the year  value when the year occurs in a ui field by itself and is not part of the day/month field
     * @param val
     */
    protected void setSeparateYear(int val)
    {
   	    separateYear = val; 
    }
    
    //---------------------------------------------------------------------
    /**
       Gets the input date
       @return the parsed date
    */
    //---------------------------------------------------------------------
    public synchronized Date getParsedDate()
    {
        Date parsedDate = null;
        try
        {
            String text = getText(0, getLength());
            parsedDate = dateFormat.parse(text);
        }
        catch (ParseException excp)
        {
            parsedDate = null;
        }
        catch (BadLocationException excp1)
        {
            parsedDate = null;
        }
        return parsedDate;
    }

    //---------------------------------------------------------------------
    /**
       Gets the input date
       @return the parsed date
    */
    //---------------------------------------------------------------------
    public Calendar getCalendarDate()
    {
        GregorianCalendar cal = new GregorianCalendar();
        Date parsedDate = getParsedDate();
        if ( parsedDate !=null)
        { 
           cal.setTime(parsedDate);
        }
        return cal;
    }
    //---------------------------------------------------------------------
    /**
       Sets the calendar date
       @param cal the calendar object
    */
    //---------------------------------------------------------------------
     public void setCalendar(Calendar cal)
    {
        dateFormat.setCalendar(cal); 
    } 
     

}

