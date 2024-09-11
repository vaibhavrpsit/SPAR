/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/DecimalTextField.java /main/13 2011/12/05 12:16:31 cgreene Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   12/05/11 - updated from deprecated packages and used more
 *                         bigdecimal constants
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *   4    360Commerce 1.3         1/22/2006 11:45:23 AM  Ron W. Haight
 *        removed references to com.ibm.math.BigDecimal
 *   3    360Commerce 1.2         3/31/2005 4:27:42 PM   Robert Pearse   
 *   2    360Commerce 1.1         3/10/2005 10:20:52 AM  Robert Pearse   
 *   1    360Commerce 1.0         2/11/2005 12:10:31 PM  Robert Pearse   
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
 *  Revision 1.1.1.1  2004/02/11 01:04:22  cschellenger
 *  updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.2   Dec 01 2003 14:33:30   baa
 * fix formating of decimal positions
 * Resolution for 3486: Decimal point appears on Trans Tax Override, req state must enter decimal point
 * 
 *    Rev 1.0.1.0   Oct 28 2003 13:43:20   baa
 * refactor  decimal formatting
 * 
 *    Rev 1.0   Aug 29 2003 16:10:06   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.7   28 Aug 2003 00:34:56   baa
 * remove call to super.getText()
 * 
 *    Rev 1.5   12 Jul 2003 23:04:58   baa
 * Remove system outs
 * 
 *    Rev 1.4   Mar 07 2003 17:11:08   baa
 * code review changes for I18n
 * Resolution for POS SCR-1740: Code base Conversions
 * 
 *    Rev 1.3   Sep 23 2002 13:26:44   baa
 * fix decimal field
 * Resolution for POS SCR-1740: Code base Conversions
 * 
 *    Rev 1.2   Sep 20 2002 18:03:06   baa
 * country/state fixes and other I18n changes
 * Resolution for POS SCR-1740: Code base Conversions
 * 
 *    Rev 1.1   Aug 07 2002 19:34:16   baa
 * remove hard coded date formats
 * Resolution for POS SCR-1740: Code base Conversions
 * 
 *    Rev 1.0   Apr 29 2002 14:53:12   msg
 * Initial revision.
 * 
 *    Rev 1.1   25 Apr 2002 18:52:20   pdd
 * Removed unnecessary BigDecimal instantiations.
 * Resolution for POS SCR-1610: Remove inefficient instantiations of BigDecimal
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.beans;
// java imports
import java.text.DecimalFormat;

import javax.swing.text.Document;

import oracle.retail.stores.domain.utility.LocaleUtilities;
import oracle.retail.stores.foundation.utility.Util;
import java.math.BigDecimal;
//-------------------------------------------------------------------------
/**
   This field allows decimal numbers only.
*/
//-------------------------------------------------------------------------
public class DecimalTextField extends ValidatingTextField
{
    /** revision number supplied by Team Connection */
    public static final String revisionNumber = "$Revision: /main/13 $";
    /** the length of the text field without radix point */
    protected int maxLength;

    //---------------------------------------------------------------------
    /**
       Constructor.
    */
    //---------------------------------------------------------------------
    public DecimalTextField()
    {
        this("");
    }

    //---------------------------------------------------------------------
    /**
       Constructor.
       @param value the default text for the field
    */
    //---------------------------------------------------------------------
    public DecimalTextField(String value)
    {
        this(value, Integer.MAX_VALUE - 1, 2);
    }

    //---------------------------------------------------------------------
    /**
       Constructor.
       @param value the default text for the field
       @param maxLength the maximum length of the whole field
       @param decLenght the maximum length of the decimal part
    */
    //---------------------------------------------------------------------
    public DecimalTextField(String value, int maxLength)
    {
        this (value, maxLength, 2);
    }

    //---------------------------------------------------------------------
    /**
       Constructor.
       @param value the default text for the field
       @param maxLength the maximum length of the whole field
       @param decLenght the maximum length of the decimal part
    */
    //---------------------------------------------------------------------
    public DecimalTextField(String value, int maxLength, int decLength)
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
        ((DecimalDocument)getDocument()).setDecimalLength(decLength);
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
        ((DecimalDocument)getDocument()).setMaxLength(maxLength);
    }
    //---------------------------------------------------------------------
    /**
    */
    //---------------------------------------------------------------------
    public int getMaxLength()
    {
        return maxLength;
    }
    
    //---------------------------------------------------------------------
    /**
       Gets the default model for the decimal text field
       @return the model for length constrained decimal fields
    */
    //---------------------------------------------------------------------
    protected Document createDefaultModel()
    {
        return new DecimalDocument(Integer.MAX_VALUE);
    }
    
    //---------------------------------------------------------------------
    /**
       Gets the value in the field as a BigDecimal.
       @return the value in the field as a BigDecimal
    */
    //---------------------------------------------------------------------
    public BigDecimal getDecimalValue()
    {
        BigDecimal value = BigDecimal.ZERO;
        String text = getText();
        if (!Util.isEmpty(text))
        {
            if (getDocument() instanceof DecimalDocument)
            {
                DecimalDocument doc = (DecimalDocument)getDocument();
                // retrieve decimal format from document.
                DecimalFormat df = doc.getFormat();             

                Number numValue = LocaleUtilities.parseNumber(text,df);
                if (numValue != null)
                {
                   value = new BigDecimal(numValue.toString());
                }
            }
        }
        return value;
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
        boolean edit = isEditable();
        setEditable(true);
        DecimalDocument doc = (DecimalDocument)getDocument();
        // retrieve decimal format from document.
        DecimalFormat df = doc.getFormat();
       
        setText(df.format(value));
        setEditable(edit);
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
         if (getDocument() instanceof DecimalDocument)
         {
             rv = ((DecimalDocument)getDocument()).isNegativeAllowed();
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
        if (getDocument() instanceof DecimalDocument)
        {
            ((DecimalDocument)getDocument()).setNegativeAllowed(negativeAllowed);
        }
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
            ((DecimalDocument)getDocument()).setZeroAllowed(zeroAllowed);
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
            rv = ((DecimalDocument)getDocument()).isZeroAllowed();
        }

        return rv;
    }
}
