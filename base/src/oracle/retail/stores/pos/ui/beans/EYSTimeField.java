/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/EYSTimeField.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:57 mszekely Exp $
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
 *   3    360Commerce 1.2         3/31/2005 4:28:09 PM   Robert Pearse   
 *   2    360Commerce 1.1         3/10/2005 10:21:36 AM  Robert Pearse   
 *   1    360Commerce 1.0         2/11/2005 12:11:00 PM  Robert Pearse   
 *
 *  Revision 1.4  2004/04/09 13:59:07  cdb
 *  @scr 4206 Cleaned up class headers for logs and revisions.
 *
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.beans;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;

import javax.swing.JLabel;
import javax.swing.text.Document;

import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.utility.EYSDate;
import oracle.retail.stores.foundation.utility.Util;

//-------------------------------------------------------------------------
/**
   This field allows Times to be input.
   @version $KW=@(#); $Ver=pos_4.5.0:2; $EKW;
*/
//-------------------------------------------------------------------------
public class EYSTimeField extends ValidatingTextField
{
    /** revision number supplied by Team Connection */
    public static final String revisionNumber = "$KW=@(#); $Ver; $EKW;";

    /** the default time */
    public static final int DEFAULT_TIME = 1;
    /** the default hour */
    public static final int DEFAULT_HOUR = 0;
    /** the default minute */
    public static final int DEFAULT_MINUTE = 0;

    /** the code for minute */
    public static final String MINUTE   = TimeDocument.MINUTE;
    /** the code for hour */
    public static final String HOUR = TimeDocument.HOUR;

    //---------------------------------------------------------------------
    /**
       Constructor.
    */
    //---------------------------------------------------------------------
    public EYSTimeField()
    {
        this(TimeDocument.HOUR, TimeDocument.MINUTE);
    }

    //---------------------------------------------------------------------
    /**
       Constructor.
       @param cal the default time for the field
    */
    //---------------------------------------------------------------------
    public EYSTimeField(EYSDate eysdate)
    {
        this(eysdate, TimeDocument.HOUR, TimeDocument.MINUTE);
    }

    //---------------------------------------------------------------------
    /**
       Constructs a EYSTimeField with the given time and format.
       @param field1 the first field of the format
       @param field2 the second field of the format
    */
    //---------------------------------------------------------------------
    public EYSTimeField(String field1, String field2)
    {
        super("");
        if (getDocument() instanceof TimeDocument)
        {
            TimeDocument doc = (TimeDocument)getDocument();
            doc.setFormat(field1, field2);
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
            doc.addFieldListener(l);
        }
    }

    //---------------------------------------------------------------------
    /**
       Constructs a EYSTimeField with the given time and format.
       @param cal the time
       @param field1 the first field of the format
       @param field2 the second field of the format
    */
    //---------------------------------------------------------------------
    public EYSTimeField(EYSDate eysdate, String field1, String field2)
    {
        super("");
        if (getDocument() instanceof TimeDocument)
        {
            TimeDocument doc = (TimeDocument)getDocument();
            doc.setFormat(field1, field2);
            ActionListener l = new ActionListener()
            {
                public void actionPerformed(ActionEvent evt)
                {
                    setCaretPosition(getCaretPosition() + 1);
                }
            };
            doc.addFieldListener(l);
            setTime(eysdate);
        }
    }

    //---------------------------------------------------------------------
    /**
       Gets the default model for the decimal text field
       @return the model for length constrained decimal fields
    */
    //---------------------------------------------------------------------
    protected Document createDefaultModel()
    {
        return new TimeDocument();
    }

    //---------------------------------------------------------------------
    /**
       Gets the time value of the field.
       @return the time in the field
    */
    //---------------------------------------------------------------------
    public EYSDate getEYSDate()
    {
        int hour = DEFAULT_HOUR;
        int minute = DEFAULT_MINUTE;
        if (getDocument() instanceof TimeDocument)
        {
            TimeDocument doc = (TimeDocument)getDocument();
            hour = doc.getHour();
            if (hour == -1)
            {
                hour = DEFAULT_HOUR;
            }
            minute = doc.getMinutes();
            if (minute == -1)
            {
                minute = DEFAULT_MINUTE;
            }
        }
        EYSDate eysdate = DomainGateway.getFactory().getEYSDateInstance();
        eysdate.setType(EYSDate.TYPE_TIME_ONLY);
        eysdate.setHour(hour);
        eysdate.setMinute(minute);

        return(eysdate);
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
        if (getDocument() instanceof TimeDocument)
        {
            TimeDocument doc = (TimeDocument)getDocument();
            if ((doc.isHourInFormat() && doc.getHour() == -1) ||
                (doc.isMinuteInFormat() && doc.getMinutes() == -1))
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
       Sets the value of the field.
       @param cal the time
    */
    //---------------------------------------------------------------------
    public void setTime(EYSDate eysdate)
    {
        if (getDocument() instanceof TimeDocument)
        {
            TimeDocument doc = (TimeDocument)getDocument();
            SimpleDateFormat format = new SimpleDateFormat(doc.getFormat());
            setText(format.format(eysdate.dateValue()));
        }
        else
        {
            setText(eysdate.toString());
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
        String strResult = new String("Class: EYSTimeField (Revision " +
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
