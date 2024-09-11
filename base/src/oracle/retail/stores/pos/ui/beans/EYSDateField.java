/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/EYSDateField.java /main/16 2011/12/05 12:16:23 cgreene Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   12/05/11 - updated from deprecated packages and used more
 *                         bigdecimal constants
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/26/10 - XbranchMerge cgreene_tech43 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    cgreene   04/02/10 - remove deprecated LocaleContantsIfc and currencies
 *    abondala  01/03/10 - update header date
 *    mkochumm  02/11/09 - use default locale
 *
 * ===========================================================================
 * $Log:
 *    8    360Commerce 1.7         8/9/2007 4:05:01 PM    Mathews Kochummen
 *         handle dates with month and year
 *    7    360Commerce 1.6         8/3/2007 5:31:58 PM    Mathews Kochummen
 *         handle separate year field
 *    6    360Commerce 1.5         7/30/2007 4:52:06 PM   Mathews Kochummen
 *         handle customer birthdate
 *    5    360Commerce 1.4         7/27/2007 2:51:12 PM   Ashok.Mondal    CR
 *         27948 :Fixing validation of expiration date field for customer
 *         driving license.
 *    4    360Commerce 1.3         7/23/2007 3:51:20 PM   Mathews Kochummen
 *         validate date
 *    3    360Commerce 1.2         3/31/2005 4:28:08 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:21:33 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:10:59 PM  Robert Pearse   
 *
 *   Revision 1.4  2004/03/16 17:15:22  build
 *   Forcing head revision
 *
 *   Revision 1.3  2004/03/16 17:15:17  build
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 20:56:26  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:22  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 16:10:32   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.3   Jul 01 2003 20:24:26   blj
 * Put in a fix for February dates greater than 29th.
 * Resolution for 2556: POS allows users to enter invalid business dates
 * 
 *    Rev 1.2   Aug 28 2002 17:05:22   dfh
 * remove setlabel - error dialog problem
 * Resolution for POS SCR-1760: Layaway feature updates
 * 
 *    Rev 1.1   Aug 07 2002 19:34:18   baa
 * remove hard coded date formats
 * Resolution for POS SCR-1740: Code base Conversions
 * 
 *    Rev 1.0   Apr 29 2002 14:50:26   msg
 * Initial revision.
 * 
 *    Rev 1.1   15 Apr 2002 09:34:38   baa
 * make call to setLabel() from the updatePropertyFields() method
 * Resolution for POS SCR-1599: Field name labels on dialog screens use default text instead of text from bundles
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.beans;

// java imports
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

import javax.swing.text.BadLocationException;
import javax.swing.text.Document;

import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.utility.EYSDate;
import oracle.retail.stores.domain.utility.LocaleConstantsIfc;
import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.foundation.utility.Util;


//-------------------------------------------------------------------------
/**
   This field allows dates to be input and handles them using the EYSDate class.
   @version $Revision: /main/16 $
*/
//-------------------------------------------------------------------------
public class EYSDateField extends ValidatingTextField
{
    /**
        the default year
        @deprecated as of release 5.5
    **/
    public static final int DEFAULT_YEAR = 1970;
    /**
        the default month
        @deprecated as of release 5.5
    **/
    public static final int DEFAULT_MONTH = 0;
    /**
        the default date
        @deprecated as of release 5.5
    **/
    public static final int DEFAULT_DATE = 1;
    /**
        the code for month
    **/
    public static final String MONTH = DateDocument.MONTH;
    /**
        the code for day
    **/
    public static final String DAY   = DateDocument.DAY;
    /**
        the code for four-digit year
        @deprecated as of release 5.5
    **/
    public static final String YEAR4 = DateDocument.YEAR4;
    /**
        the code for two-digit year
        @deprecated as of release 5.5
    **/
    public static final String YEAR2 = DateDocument.YEAR2;
    
    public static final String revisionNumber = "$Revision: /main/16 $";
    
   
   //---------------------------------------------------------------------
    /**
      Default constructor
    */
    //---------------------------------------------------------------------
    public EYSDateField()
    {                                   // begin EYSDate()
        this(null); // end handle date document
    } 

 
    //---------------------------------------------------------------------
    /**
       Constructs a EYSDateField with the given date and format. <P>
       @param eysdate the date
       @param format the dateformat style
    */
    //---------------------------------------------------------------------
    public EYSDateField(EYSDate eysdate)
    {                                   // begin EYSDate()
        super("");
        if (getDocument() instanceof DateDocument)
        {                               // begin handle date document
            DateDocument doc = (DateDocument) getDocument();
            doc.setLocale(getLocale());
            doc.setFormat(DateFormat.SHORT);
            ActionListener l = new ActionListener()
            {                           // begin action listener inner class
                public void actionPerformed(ActionEvent evt)
                {
                    int pos = getCaretPosition();
                    if (pos < getText().length())
                    {
                        setCaretPosition(pos + 1);
                    }
                }
            };                          // end action listener inner class
            doc.addFieldListener(l);
            if (eysdate != null)
            { 
              setDate(eysdate);
            }
        }                               // end handle date document

    }

    //---------------------------------------------------------------------
    /**
       Constructs an EYSDateField with the given date and format. <P>
       @param field1 the first field of the format
       @param field2 the second field of the format
       @param field3 the third field of the format
       @deprecated as of release 5.5 replaced by EYSDateField()
    */
    //---------------------------------------------------------------------
    public EYSDateField(String field1, String field2, String field3)
    {                                   // begin EYSDate()
         this(null);             // end handle date document
    }  
    //---------------------------------------------------------------------
    /**
       Constructs a EYSDateField with the given date and format. <P>
       @param eysdate the date
       @param field1 the first field of the format
       @param field2 the second field of the format
       @param field3 the third field of the format
       @deprecated as of release 5.5 replaced by EYSDateField(EYSDate eysdate)
    */
    //---------------------------------------------------------------------
    public EYSDateField(EYSDate eysdate, String field1, String field2, String field3)
    {                                   // begin EYSDate()
       this(eysdate);
    }     

                             // end getEYSDate()
    //---------------------------------------------------------------------
    /**
       Gets the default model for the date text field
       @return the model for length constrained decimal fields
    */
    //---------------------------------------------------------------------
    protected Document createDefaultModel()
    {
        return new DateDocument(DateFormat.SHORT,getLocale());
    }

      //---------------------------------------------------------------------
    /**
       Gets the date value. If no date is available it returns
       the current date as default.
       @return the date in the field
    */
    //---------------------------------------------------------------------
    public void setFormat(int style)
    {                                   // begin getEYSDate()
        if (getDocument() instanceof DateDocument)
        {                               // handle date document
            DateDocument doc = (DateDocument) getDocument();
            doc.setFormat(style);
        }                               // end handle date document
     }   
    //---------------------------------------------------------------------
    /**
       Determines whether the current field information is valid and
       returns the result. <P>
       @return true if the current field entry is valid, false otherwise
    */
    //---------------------------------------------------------------------
    public boolean isInputValid()
    { 

        boolean rv = false;
        if (getDocument() instanceof DateDocument)
        {                               // begin handle date document
            DateDocument doc = (DateDocument) getDocument();

            if (!(isDayMonthYearValid(doc.getDay(), doc.getMonth(), doc.getYear(), doc.getSeparateYear())))
            {
            	rv = false;
            }
            else if ((doc.isMonthInFormat() && doc.getMonth() == -1) ||
                (doc.isDayInFormat() && doc.getDay() == -1) || 
                (doc.isYearInFormat() && !isYearValid(doc)) ||
                (!getEYSDate().isValid()))                
            {
                rv = false;
            }
            else
            {
                rv = super.isInputValid();
            }
        }                               // end handle date document
        else
        {
            rv = super.isInputValid();
        }
         return rv;
         
    }                                   // end isInputValid()

    
 
    /**
     * validates day, month,year
     * @param day
     * @param month
     * @param year
     * @param separateYear
     * @return
     */
    private boolean isDayMonthYearValid(int day, int month, int year, int separateYear)
    {    
        boolean valid = true;
        // evaluate based against calendar entries.

        // Thirty days hath September,
        // April, June, and November;
        // February has twenty-eight alone,
        // All the rest have thirty-one,
        // Excepting leap year, that's the time
        // When February's days are twenty-nine.

        switch (month)
        {                               // begin evaluate date
            // check 30-day months
            case 4:
            case 6:
            case 9:
            case 11:                
                if (day >30) 
                {
                	valid = false;
                }
                break;

            // check 31-day months
            case 1:
            case 3:
            case 5:
            case 7:
            case 8:
            case 10:
            case 12:
                if (day > 31)
                {
                        valid = false;
                }
                break;

            // check februrary
            case 2:
                // leap years have 29
                // century years don't have leap years except every 400 years
            	
                if (year % 4   == 0 &&
                    (year % 100 != 0 ||
                     year % 400 == 0))
                {
                    if (day > 29) 
                    {
                        valid = false;
                    }
                }
                // check non-leap years
                else
                {
                	if (year == -1)
                	{
                		//check the separate year field if any
                		if (separateYear != -1)
                		{
                			if (isLeapYear(separateYear))
                			{
                				if (day > 29)
                				{
                					valid = false;
                				}
                			}
                			else if (day > 28)
                			{
                				valid = false;
                			}		
                		}
                		//no separate year field
                		else if (day > 29)
                		{
                			valid = false;
                		}
                	}
                	else if (day > 28) 
                    {
                        valid = false;
                    }
                }            	
                break;

            default:
                break;
        }                              
        return(valid); 
    }                                   
   

    private boolean isLeapYear (int year)
    {
    	boolean leapYear = false;
    	if (year % 100 == 0)
    	{
    		if (year %400 == 0)
    		{
    		    leapYear = true;
    		}
    	}
    	else if (year % 4 == 0)
    	{
    		leapYear = true;
    	}
    	return leapYear;
    }

    //---------------------------------------------------------------------
    /**
       Determines whether the year is valid based on format and value.
       If 4 digit year, input must be 4 digits. <P>
       @return true if year is valid, false otherwise
    */
    //---------------------------------------------------------------------
    protected boolean isYearValid(DateDocument doc)
    {                                   // begin isYearValid()
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
    }                                   // end isYearValid()

    //---------------------------------------------------------------------
    /**
       Sets the value of the field. <P>
       @param eysdate the date
    */
    //---------------------------------------------------------------------
    public void setDate(EYSDate eysdate)
    {    
        if (eysdate !=null)
        { 
            
            if (getDocument() instanceof DateDocument)
            {
                DateDocument doc = (DateDocument)getDocument();
                int yr = eysdate.getYear();

                // check if the year format is set to four digits
                // an display accordingly
                if ( doc.isYear4Digit() && (yr < 1000))
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
                eysdate.setYear(yr);

                if (doc.fields != null && doc.fields.length == 2 && doc.year == -1 && doc.getDateFormat() instanceof SimpleDateFormat)
                {
                	//if there is no year field then interpret date as a leap year so that it can handle dates like feb 29
                	//set to any leap year.
                	eysdate.setYear(2000);
                	setText(eysdate.toFormattedString(((SimpleDateFormat)(doc.getDateFormat())).toPattern(),getLocale()));
                }
                else
                {
                	setText(eysdate.toFormattedString(doc.getFormat(),getLocale()));
                }

            }
            else
            {
                 setText(eysdate.toString());
            }
        }
        else
        {
            setText("");
        }
    }                                   // end setDate()
     //---------------------------------------------------------------------
    /**
       Gets the date value. If no date is available it returns
       the current date as default.
       @return the date in the field
    */
 
    public EYSDate getEYSDate(int dateType)
    {                                   // begin getEYSDate()
        EYSDate eysdate = DomainGateway.getFactory().getEYSDateInstance(); 
        eysdate.setType(dateType);
        if (getDocument() instanceof DateDocument)
        {                               // handle date document
            DateDocument doc = (DateDocument) getDocument();
            doc.setLocale(getLocale());
            eysdate.setType(dateType);       
           try
            {
               String text = doc.getText(0,doc.getLength());
               if (Util.isEmpty(text))
               {
                     eysdate.initialize(getLocale());
               }
               else
               {
            	     if (doc.fields != null && doc.fields.length == 2 && (doc.getDateFormat() instanceof SimpleDateFormat))
            	     {
                         if (doc.getMonth()!= -1 && doc.getDay() != -1)
            	    	 {
            	    	     //handle dates when the year is not defined or is defined in a separate field
            	    	     eysdate.initialize2(text, ((SimpleDateFormat)(doc.getDateFormat())).toPattern(), doc.getMonth(), doc.getDay());
            	    	 }
                         else if (doc.getMonth() != -1 && doc.getYear() != -1)
            	    	 {
            	    	     //handle dates when only month and year are defined
            	    	     eysdate.initialize(text, ((SimpleDateFormat)(doc.getDateFormat())).toPattern());
            	    	 }
            	     }
            	     else
            	     {
                         eysdate.initialize(text, doc.getFormat());
            	     }
               }  
            }
            catch (BadLocationException e)
            {
               eysdate = null;//eysdate.initialize(getLocale());
            }
          
        }                               // end handle date document

        return(eysdate);
    } 
    
    //---------------------------------------------------------------------
    /**
       Gets the date value. If no date is available it returns
       the current date as default.
       @return the date in the field
    */    
    //---------------------------------------------------------------------
    public EYSDate getEYSDate()
    {                                   // begin getEYSDate()
        return(getEYSDate(EYSDate.TYPE_DATE_ONLY));
    }  

    
    //---------------------------------------------------------------------
    /**
       Gets the date value of the field.  
       @return the date in the field
    */
    //---------------------------------------------------------------------
    public EYSDate getDate()
    {   
        EYSDate eysdate = DomainGateway.getFactory().getEYSDateInstance(); 
        if (getDocument() instanceof DateDocument)
        {                               // handle date document
            DateDocument doc = (DateDocument) getDocument();
            doc.setLocale(getLocale());
            try
            {
               String text = doc.getText(0,doc.getLength());
               if (Util.isEmpty(text))
               {
                     eysdate = null;
               }
               else
               {
            	   eysdate.setType(EYSDate.TYPE_DATE_ONLY);
            	   if (doc.fields != null && doc.fields.length == 2 && (doc.getDateFormat() instanceof SimpleDateFormat))
            	   {
            		   if (doc.getMonth()!= -1 && doc.getDay() != -1)
            		   {
            			   //handle dates when the year is not defined or is defined in a separate field
            			   eysdate.initialize2(text, ((SimpleDateFormat)(doc.getDateFormat())).toPattern(), doc.getMonth(), doc.getDay());
            		   }
            		   else if (doc.getMonth() != -1 && doc.getYear() != -1)
            		   {
                           // handle dates when only month and year are defined
                           eysdate.initialize(text, ((SimpleDateFormat)(doc.getDateFormat())).toPattern());
            		   }
            	   }
            	   else
            	   {
            		   eysdate.initialize(text, doc.getFormat());
            	   }
               }  
            }
            catch (BadLocationException e)
            {
               eysdate = null;
            }
       
        }                               // end handle date document
       return(eysdate);
    }  
    
    //---------------------------------------------------------------------
    /**
        Returns default store locale. <P>
        @return String representation of object
    **/
    //---------------------------------------------------------------------
    public Locale getLocale()
    {
        return(LocaleMap.getLocale(LocaleConstantsIfc.DEFAULT_LOCALE));
    }
    
    
    //---------------------------------------------------------------------
    /**
       Returns default display string. <P>
       @return String representation of object
    */
    //---------------------------------------------------------------------
    public String toString()
    {
        String strResult = new String("Class: EYSDateField (Revision " +
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

}                                       // end class EYSDateField
