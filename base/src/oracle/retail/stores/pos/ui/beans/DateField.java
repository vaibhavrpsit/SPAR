/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/DateField.java /main/18 2011/12/05 12:16:23 cgreene Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   12/05/11 - updated from deprecated packages and used more
 *                         bigdecimal constants
 *    cgreene   05/28/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
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
 *   3    360Commerce 1.2         3/31/2005 4:27:41 PM   Robert Pearse   
 *   2    360Commerce 1.1         3/10/2005 10:20:48 AM  Robert Pearse   
 *   1    360Commerce 1.0         2/11/2005 12:10:29 PM  Robert Pearse   
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
 *    Rev 1.0   Aug 29 2003 16:10:04   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.1   Aug 07 2002 19:34:16   baa
 * remove hard coded date formats
 * Resolution for POS SCR-1740: Code base Conversions
 * 
 *    Rev 1.0   Apr 29 2002 14:56:30   msg
 * Initial revision.
 * 
 *    Rev 1.1   15 Apr 2002 09:33:46   baa
 * make call to setLabel() from the updatePropertyFields() method
 * Resolution for POS SCR-1599: Field name labels on dialog screens use default text instead of text from bundles
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.beans;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;

import javax.swing.JLabel;
import javax.swing.text.Document;

import oracle.retail.stores.domain.utility.LocaleConstantsIfc;
import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.foundation.utility.Util;

//-------------------------------------------------------------------------
/**
   This field allows dates to be input.
   @version $Revision: /main/18 $
   @deprecated as of release 5.5 replaced by @oracle.retail.stores.pos.ui.beans.EYSDateField
*/
//-------------------------------------------------------------------------
public class DateField extends ValidatingTextField
{
    /** revision number supplied by Team Connection */
    public static final String revisionNumber = "$Revision: /main/18 $";

    /** the default year */
    //@deprecated as of release 5.5 get default dates from Calendar class
    public static final int DEFAULT_YEAR = 1970;
    /** the default month */
    //@deprecated as of release 5.5 get default dates from Calendar class
    public static final int DEFAULT_MONTH = 0;
    /** the default date */
    //@deprecated as of release 5.5 get default dates from Calendar class
    public static final int DEFAULT_DATE = 1;

    /** the code for month */
    //@deprecated as of release 5.5 get default dates from Calendar class
    public static final String MONTH = DateDocument.MONTH;
    /** the code for day */
    //@deprecated as of release 5.5 get default dates from Calendar class
    public static final String DAY   = DateDocument.DAY;
    /** the code for four-digit year */
    //@deprecated as of release 5.5 get default dates from Calendar class
    public static final String YEAR4 = DateDocument.YEAR4;
    /** the code for two-digit year */
    //@deprecated as of release 5.5 get default dates from Calendar class
    public static final String YEAR2 = DateDocument.YEAR2;
  
    /** 
    //---------------------------------------------------------------------
    /**
       Constructor.
    */
    //---------------------------------------------------------------------
    public DateField()
    {
       this(null);
    }

    //---------------------------------------------------------------------
    /**
       Constructor.
       @param cal the default date for the field
    */
    //---------------------------------------------------------------------
    public DateField(Calendar cal)
    {
       super("");
        if (getDocument() instanceof DateDocument)
        {
            DateDocument doc = (DateDocument)getDocument();
            doc.setFormat(DateFormat.SHORT);
            ActionListener l = new ActionListener()
            {
                public void actionPerformed(ActionEvent evt)
                {
                    int pos = getCaretPosition();
                    if (pos < getText().length())
                    {
                        setCaretPosition(pos + 1);
                    }
                }
            };
            if (cal != null)
            doc.setCalendar(cal);
            doc.addFieldListener(l);
        }
    }

    //---------------------------------------------------------------------
    /**
       Constructs a DateField with the given date and format.
       @param field1 the first field of the format
       @param field2 the second field of the format
       @param field3 the third field of the format
       @deprecated as of release 5.5 replace by DateField()
    */
    //---------------------------------------------------------------------
    public DateField(String field1, String field2, String field3)
    {
        this(null);
    }

    //---------------------------------------------------------------------
    /**
       Constructs a DateField with the given date and format.
       @param cal the date
       @param field1 the first field of the format
       @param field2 the second field of the format
       @param field3 the third field of the format
       @deprecated as of release 5.5 replace by DateField(Calendar cal)
    */
    //---------------------------------------------------------------------
    public DateField(Calendar cal, String field1, String field2, String field3)
    {
        this(cal);
    }

    //---------------------------------------------------------------------
    /**
       Gets the default model for the decimal text field
       @return the model for length constrained decimal fields
    */
    //---------------------------------------------------------------------
    protected Document createDefaultModel()
    {
    	Locale locale = LocaleMap.getLocale(LocaleConstantsIfc.DEFAULT_LOCALE);
        return new DateDocument(DateFormat.SHORT, locale);
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
        if (getDocument() instanceof DateDocument)
        {
            DateDocument doc = (DateDocument)getDocument();
            if ((doc.isMonthInFormat() && doc.getMonth() == -1) ||
                (doc.isDayInFormat() && doc.getDay() == -1) ||
                (doc.isYearInFormat() && !isYearValid(doc)))
            {
                rv = false;
            }
            else
            {
                rv = super.isInputValid();
            }
        }
        else
        {
            rv = super.isInputValid();
        }

        if (rv == false)
        {
            setText("");
        }

        return rv;
    }

    //---------------------------------------------------------------------
    /**
       Sets the label associated with the field and configures the error
       message based on the label text.
       @param label
    */
    //---------------------------------------------------------------------
    public void setLabel(JLabel label)
    {
        this.label = label;
        String displayText = this.getLabel().getText();
        if(!(displayText.indexOf(':') < 0))
        {
            displayText = displayText.substring(0,displayText.indexOf(':'));
        }

        setErrorMessage(displayText);
    }

    //---------------------------------------------------------------------
    /**
       Determines whether the year is valid
       Based on format and value if 4 digit year must be 4 digits
       @return true if year is valid, false otherwise
    */
    //---------------------------------------------------------------------
    protected boolean isYearValid(DateDocument doc)
    {
        int y = doc.getYear();
        if(y == -1)
        {
            return false;
        }
        else if( doc.isYear4Digit() && y < 1000)
        {
            return false;
        }
        return true;
    }

    //---------------------------------------------------------------------
    /**
       Gets the date value of the field.
       @return the date in the field
    */
    //---------------------------------------------------------------------
    public Calendar getDate()
    {
        int year = DEFAULT_YEAR;
        int month = DEFAULT_MONTH;
        int date = DEFAULT_DATE;
        if (getDocument() instanceof DateDocument)
        {
            DateDocument doc = (DateDocument)getDocument();
            year = doc.getYear();
            if (year == -1)
            {
                year = DEFAULT_YEAR;
            }
            month = doc.getMonth();
            if (month == -1)
            {
                month = DEFAULT_MONTH;
            }
            date = doc.getDay();
            if (date == -1)
            {
                date = DEFAULT_DATE;
            }
        }
        GregorianCalendar cal = new GregorianCalendar(year, month, date);
        return cal;
    }
    
    //---------------------------------------------------------------------
    /**
       Sets the value of the field.
       @param cal the date
    */
    //---------------------------------------------------------------------
    public void setDate(Calendar cal)
    {
        if (getDocument() instanceof DateDocument)
        {
            DateDocument doc = (DateDocument)getDocument();
            SimpleDateFormat format = new SimpleDateFormat(doc.getFormat());
            setText(format.format(cal.getTime()));
        }
        else
        {
            setText(cal.toString());
        }
    }
    //---------------------------------------------------------------------
    /**
       Returns default display string. <P>
       @return String representation of object
    */
    //---------------------------------------------------------------------
    public String toString()
    {
        String strResult = new String("Class: DateField (Revision " +
                                      getRevisionNumber() + ") @" +
                                      hashCode());
        return(strResult);
    }

    //---------------------------------------------------------------------
    /**
       Retrieves the Team Connection revision number. <P>
       @return String representation of revision number
    */
    //---------------------------------------------------------------------
    public String getRevisionNumber()
    {
        return(Util.parseRevisionNumber(revisionNumber));
    }

}
