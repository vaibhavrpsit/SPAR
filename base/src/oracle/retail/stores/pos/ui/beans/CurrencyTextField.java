/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/CurrencyTextField.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:45 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    acadar    06/10/10 - use default locale for currency display
 *    acadar    06/09/10 - XbranchMerge acadar_tech30 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/26/10 - XbranchMerge cgreene_tech43 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    cgreene   04/02/10 - remove deprecated LocaleContantsIfc and currencies
 *    abondala  01/03/10 - update header date
 *    acadar    04/21/09 - provide mechanism to translate the date/time pattern
 *
 * ===========================================================================
 * $Log:
 *    13   360Commerce 1.12        7/9/2007 3:07:52 PM    Anda D. Cadar   I18N
 *         changes for CR 27494: POS 1st initialization when Server is offline
 *    12   360Commerce 1.11        7/3/2007 9:50:34 AM    Anda D. Cadar   get
 *         the number of decimal digits from base currency type
 *    11   360Commerce 1.10        6/19/2007 1:58:11 PM   Anda D. Cadar
 *         additional cleanup for I18N
 *    10   360Commerce 1.9         5/18/2007 9:18:15 AM   Anda D. Cadar   EJ
 *         and currency UI changes
 *    9    360Commerce 1.8         4/25/2007 8:51:30 AM   Anda D. Cadar   I18N
 *         merge
 *    8    360Commerce 1.7         4/4/2007 12:09:08 PM   Michael Boyd
 *         v7.2.2 merge to trunk - CR 26172
 *
 *
 *         9    .v7x      1.6.1.1     8/25/2006 5:55:42 AM   Dinesh Gautam
 *         CR
 *         10782: Refix done.
 *         8    .v7x      1.6.1.0     7/19/2006 7:30:08 AM   Dinesh Gautam
 *         CR10782
 *         - Refix for User Locale.
 *         7    360Commerce1.6         3/20/2006 7:29:18 AM   Akhilashwar K.
 *         Gupta
 *    7    360Commerce 1.6         3/20/2006 7:29:18 AM   Akhilashwar K. Gupta
 *         CR-10782: Updated "setCurrencyValue()" method to check for Locale.
 *    6    360Commerce 1.5         1/25/2006 4:10:54 PM   Brett J. Larsen merge
 *          7.1.1 changes (aka. 7.0.3 fixes) into 360Commerce view
 *    5    360Commerce 1.4         1/22/2006 11:45:22 AM  Ron W. Haight
 *         removed references to com.ibm.math.BigDecimal
 *    4    360Commerce 1.3         12/13/2005 4:42:43 PM  Barry A. Pape
 *         Base-lining of 7.1_LA
 *    3    360Commerce 1.2         3/31/2005 4:27:34 PM   Robert Pearse
 *    2    360Commerce 1.1         3/10/2005 10:20:32 AM  Robert Pearse
 *    1    360Commerce 1.0         2/11/2005 12:10:17 PM  Robert Pearse
 *:
 *    5    .v700     1.2.1.1     9/29/2005 10:55:32     Rohit Sachdeva  Select
 *         Tender screen does not handle negative values....displays a blank
 *         field which also results in a blank Total field
 *    4    .v700     1.2.1.0     9/19/2005 13:48:09     Jason L. DeLeau Make
 *         sure CurrencyTextFields can have a blank default value.
 *    3    360Commerce1.2         3/31/2005 15:27:34     Robert Pearse
 *    2    360Commerce1.1         3/10/2005 10:20:32     Robert Pearse
 *    1    360Commerce1.0         2/11/2005 12:10:17     Robert Pearse
 *
 *         Base-lining of 7.1_LA
 *    3    360Commerce1.2         3/31/2005 3:27:34 PM   Robert Pearse
 *    2    360Commerce1.1         3/10/2005 10:20:32 AM  Robert Pearse
 *    1    360Commerce1.0         2/11/2005 12:10:17 PM  Robert Pearse
 *: CurrencyTextField.java,v $
 *
 *:
 *    5    .v710     1.2.2.1     10/24/2005 14:20:53    Charles Suehs   Merged
 *         from .v700 to fix CR 3965.
 *    4    .v710     1.2.2.0     10/20/2005 18:24:00    Charles Suehs   Merge
 *         from CurrencyTextField.java, Revision 1.2.1.1
 *    3    360Commerce1.2         3/31/2005 15:27:34     Robert Pearse
 *    2    360Commerce1.1         3/10/2005 10:20:32     Robert Pearse
 *    1    360Commerce1.0         2/11/2005 12:10:17     Robert Pearse
 *
 *   Revision 1.4  2004/03/16 17:15:22  build
 *   Forcing head revision
 *
 *   Revision 1.3  2004/03/16 17:15:17  build
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 20:56:27  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:21  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 *
 *    Rev 1.2   Dec 01 2003 14:33:36   baa
 * fix formating of decimal positions
 * Resolution for 3486: Decimal point appears on Trans Tax Override, req state must enter decimal point
 *
 *    Rev 1.0.1.0   Oct 28 2003 13:43:14   baa
 * refactor  decimal formatting
 *
 *    Rev 1.7   17 Aug 2003 22:24:44   baa
 * currency formatting issues
 *
 *    Rev 1.6   01 Jul 2003 23:22:38   baa
 * handle formatting negative currency numbers
 *
 *    Rev 1.5   Mar 07 2003 17:11:08   baa
 * code review changes for I18n
 * Resolution for POS SCR-1740: Code base Conversions
 *
 *    Rev 1.4   Sep 20 2002 17:56:28   baa
 * country/state fixes and other I18n changes
 * Resolution for POS SCR-1740: Code base Conversions
 *
 *    Rev 1.3   Aug 14 2002 18:17:00   baa
 * format currency
 * Resolution for POS SCR-1740: Code base Conversions
 *
 *    Rev 1.2   Aug 07 2002 19:34:14   baa
 * remove hard coded date formats
 * Resolution for POS SCR-1740: Code base Conversions
 *
 *    Rev 1.1   Jun 21 2002 18:26:22   baa
 * externalize parameter names,
 * start formatting currency base on locale
 * Resolution for POS SCR-1624: Localization Support
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.beans;
//java imports


import javax.swing.text.Document;

import org.apache.log4j.Logger;

import java.math.BigDecimal;

import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.common.utility.LocaleMap;

//domain imports
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.utility.LocaleUtilities;
import oracle.retail.stores.foundation.utility.Util;

//-------------------------------------------------------------------------
/**
   This field allows decimal numbers only.
*/
//-------------------------------------------------------------------------
public class CurrencyTextField extends ValidatingTextField
{
    /**
     *
     */
    private static final long serialVersionUID = 1689550536067548328L;
    /**
        revision number supplied by Team Connection
    **/
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";
    /**
        the length of the text field without radix point
    **/
     protected int maxLength;

     /**
      *
      */
     protected static int maxFractionDigits;



     /** Store instance of logger here **/
     protected static Logger logger = Logger.getLogger(oracle.retail.stores.pos.ui.beans.CurrencyTextField.class);


     //---------------------------------------------------------------------
    /**
       Constructor.
    */
    //---------------------------------------------------------------------
    public CurrencyTextField()
    {
        this("");

    }


    //---------------------------------------------------------------------
    /**
       Constructor.
       @param value the default text for the field
    */
    //---------------------------------------------------------------------
    public CurrencyTextField(String value)
    {
        this(value, Integer.MAX_VALUE - 1, getMaxFractionDigits());
    }

    //---------------------------------------------------------------------
    /**
       Constructor.
       @param value the default text for the field
       @param maxLength the maximum length of the whole field
       @param decLenght the maximum length of the decimal part
    */
    //---------------------------------------------------------------------
    public CurrencyTextField(String value, int maxLength)
    {
        this (value, maxLength, getMaxFractionDigits());
    }
    //---------------------------------------------------------------------
    /**
       Constructor.
       @param value the default text for the field
       @param maxLength the maximum length of the whole field
       @param decLenght the maximum length of the decimal part
    */
    //---------------------------------------------------------------------
    public CurrencyTextField(String value, int maxLength, int decLength)
    {

       super(value);
       setMaxLength(maxLength);
       setDecimalLength(decLength);

    }

    //---------------------------------------------------------------------
    /** Sets the length of the decimal part of this fields. How many decimal
     *  positions.
     * @param decLength  integer value that represents the number of digits
     * after the decimal separator
    */
    //---------------------------------------------------------------------
    public void setDecimalLength(int decLength)
    {
        ((CurrencyDocument)getDocument()).setDecimalLength(decLength);
    }
    //---------------------------------------------------------------------
    /**
     * Sets the maximum length for the field
     * @param maxLength  integer value that represents the max length for
     *                   this field
    */
    //---------------------------------------------------------------------
    public void setMaxLength(int maxLength)
    {
        if (maxLength >= Integer.MAX_VALUE)
        {
            maxLength -= 1;
        }
        this.maxLength = maxLength;
        ((CurrencyDocument)getDocument()).setMaxLength(maxLength);
    }
    //---------------------------------------------------------------------
    /**
       Gets the default model for the decimal text field
       @return the model for length constrained decimal fields
    */
    //---------------------------------------------------------------------
    protected Document createDefaultModel()
    {
        return new CurrencyDocument();
    }

    //---------------------------------------------------------------------
    /**
       Gets the value in the field as a Currency Field.
       @return the value in the field as CurrencyIfc
    */
    //---------------------------------------------------------------------
    public CurrencyIfc getCurrencyValue()
    {
    	CurrencyIfc c = null;
    	if(getDecimalValue() != null)
    	{
    		c = DomainGateway.getBaseCurrencyInstance();
    		c.setDecimalValue(getDecimalValue());
    	}
    	return c;
    }


    //---------------------------------------------------------------------
    /**
       Sets the value of the field as a BigDecimal. The value will be truncated
       to the number of decimal spaces set for this document.
       @param value the value of the field as a BigDecimal
    */
    //---------------------------------------------------------------------
    public void setDecimalValue(BigDecimal value)
    {
        if (value != null)
        {
            CurrencyIfc currencyVal = DomainGateway.getBaseCurrencyInstance(value);
            setCurrencyValue(currencyVal);
        }
        else
        {
        	setCurrencyValue(null);
        }
    }
     //---------------------------------------------------------------------
    /**
       Sets the value of the field using CurrencyIfc. The value will be
       truncated to the number of decimal spaces set for this document.
       @param value the value of the field as a CurrencyIfc
    */
    //---------------------------------------------------------------------
    public void setValue(CurrencyIfc dollar)
    {
        setCurrencyValue(dollar);
    }
     //---------------------------------------------------------------------
    /**
       Sets the value of the field as a BigDecimal. The value will be truncated
       to the number of decimal spaces set for this document.
       @param value the value of the field as a BigDecimal
    */
    //---------------------------------------------------------------------
    public void setCurrencyValue(CurrencyIfc value)
    {
        if (value != null)
        {
            if (getDocument() instanceof CurrencyDocument)
            {
                CurrencyDocument doc = (CurrencyDocument)getDocument();
                setCaretPosition(doc.getLength());

                boolean edit = isEditable();   // save editable property of the field
                setEditable(true);
                String text = LocaleUtilities.formatCurrency(value.getDecimalValue(),LocaleMap.getLocale(LocaleMap.DEFAULT), false );
                super.setText(text);
                setEditable(edit);
            }
            else
            {
               super.setText(value.toFormattedString());
            }
        }
        else
        {
        	super.setText("");
        }
    }
    //---------------------------------------------------------------------
     /**
        Gets the value in the field as a BigDecimal.
        @return the value in the field as a BigDecimal
     */
     //---------------------------------------------------------------------
     public BigDecimal getDecimalValue()
     {
         BigDecimal value = null;
         String text = getText();
         if (!Util.isEmpty(text))
         {
             if (getDocument() instanceof CurrencyDocument)
             {
                 CurrencyDocument doc = (CurrencyDocument)getDocument();
                 value = (BigDecimal)LocaleUtilities.parseCurrency(text, LocaleMap.getLocale(LocaleMap.DEFAULT));

             }

         }
         return value;
     }
   //---------------------------------------------------------------------
    /**
       Returns the format use to display the field
       @return the format use to display the field
    */
    //---------------------------------------------------------------------
     public String getPattern()
    {
        CurrencyDocument doc = (CurrencyDocument)getDocument();
        doc.setupDecimalFormat();
        return doc.getFormat().toPattern();
    }
    //---------------------------------------------------------------------
    /**
       Sets the flag to allow or disallow a zero value.
       @param zeroAllowed true if a zero value should be allowed,
       false otherwise
    */
    //---------------------------------------------------------------------
    public void setZeroAllowed(boolean zeroAllowed)
    {
        if (getDocument() instanceof DecimalDocument)
        {
            ((CurrencyDocument)getDocument()).setZeroAllowed(zeroAllowed);
        }
    }
    //---------------------------------------------------------------------
    /**
       Returns whether a zero value is allowed in this field.
       @return true if zero value is allowed, false otherwise
    */
    //---------------------------------------------------------------------
    public boolean isZeroAllowed()
    {
        boolean rv = true;

        if (getDocument() instanceof DecimalDocument)
        {
            rv = ((CurrencyDocument)getDocument()).isZeroAllowed();
        }

        return rv;
    }
    //---------------------------------------------------------------------
     /**
        Returns whether negative values are allowed in this field.
        @return true if negative numbers allowed, false otherwise
     */
     //---------------------------------------------------------------------
     protected boolean isNegativeAllowed()
     {
         boolean rv;
         if (getDocument() instanceof CurrencyDocument)
         {
             rv = ((CurrencyDocument)getDocument()).isNegativeAllowed();
         }
         else
         {
             rv = true;
         }
         return rv;
     }

    //---------------------------------------------------------------------
    /**
       Sets the flag to allow or disallow negative numbers.
       @param negativeAllowed true if negative numbers should be allowed,
       false otherwise
    */
    //---------------------------------------------------------------------
    public void setNegativeAllowed(boolean negativeAllowed)
    {
        if (getDocument() instanceof CurrencyDocument)
        {
            ((CurrencyDocument)getDocument()).setNegativeAllowed(negativeAllowed);
        }
    }

    /**
     * Gets the number of digits after the decimal point
     * @return
     */
    public static int getMaxFractionDigits()
    {
        maxFractionDigits = 2;
        try
        {
            maxFractionDigits = DomainGateway.getBaseCurrencyType().getScale();
        }
        catch (Exception e)
        {
            logger.error("Currency Information is not available, database/server may be offline");
        }
        return maxFractionDigits;
    }
    //---------------------------------------------------------------------
    /**
     * Sets the formatted value for checkAmount field
     * @param checkAmount check amount value
     * @param isEditable editable field property flag
     */
    //---------------------------------------------------------------------

    public void setText(String checkAmount, boolean isEditable)
    {
        if (getDocument() instanceof CurrencyDocument)
        {
            ((CurrencyDocument)getDocument()).setEditable(isEditable);
        }
        super.setText(checkAmount);
    }
}
