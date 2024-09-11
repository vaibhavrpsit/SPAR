/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/NumericDecimalTextField.java /main/14 2011/12/05 12:16:31 cgreene Exp $
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
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    5    360Commerce 1.4         8/8/2007 5:52:17 PM    Michael P. Barnett
 *         Require max length of document rather than hardcode value.
 *    4    360Commerce 1.3         1/22/2006 11:45:27 AM  Ron W. Haight
 *         removed references to com.ibm.math.BigDecimal
 *    3    360Commerce 1.2         3/31/2005 4:29:10 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:23:44 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:12:47 PM  Robert Pearse   
 *
 *   Revision 1.5  2004/04/21 19:57:54  rsachdeva
 *   @scr 3906 JavaDoc comment added
 *
 *   Revision 1.4  2004/03/16 17:15:18  build
 *   Forcing head revision
 *
 *   Revision 1.3  2004/03/11 15:07:11  rsachdeva
 *   @scr 3906 Error Message
 *
 *   Revision 1.2  2004/03/09 15:21:46  rsachdeva
 *   @scr 3906 Document
 *
 *   Revision 1.1  2004/03/04 19:16:16  rsachdeva
 *   @scr 3906 Initial Revision
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.beans;

import javax.swing.JTextField;
import java.text.DecimalFormat;
import oracle.retail.stores.domain.utility.LocaleUtilities;
import oracle.retail.stores.foundation.utility.Util;
import java.math.BigDecimal;


//-------------------------------------------------------------------------
/**
   This field allows numeric or decimal numbers depending on the document
   being set<p>
   @version "$Revision: /main/14 $"
**/
//-------------------------------------------------------------------------
public class NumericDecimalTextField extends ValidatingTextField
{
    /** 
       revision number 
    **/
    public static final String revisionNumber = "$Revision: /main/14 $";
    

    //---------------------------------------------------------------------
    /**
        Class Constructor.
        @param value the default text for the field
    **/
    //---------------------------------------------------------------------
    public NumericDecimalTextField(String value, int maxLength, boolean zeroAllowed)
    {
        super(value);
        setRequired(true);
        setDocument(getNumericDocument(maxLength, zeroAllowed));
        setHorizontalAlignment(JTextField.RIGHT);
        setEditable(true);
    }

    
    //---------------------------------------------------------------------
    /**
        Determines whether the current field information is valid and
        returns the result.
        @return boolean true if the current field entry is valid, false otherwise
    **/
    //---------------------------------------------------------------------
    public boolean isInputValid()
    {
        // reset error message in case we've changed it
        //This sets the default error message of a field
        setErrorMessage();

        boolean rv = false;
        if (super.isInputValid())
        {
            String text = getText();
            if (getDocument() instanceof DecimalDocument)
            {
                BigDecimal value = getDecimalValue();
                      
                if (text.length() > 0 && value.signum() > 0)
                {
                    rv = true;
                }
           }
           if (getDocument() instanceof NumericDocument)
           {
                if (text.length() > 0)
                {
                    rv = true;
                }
           }
        }
        return rv;
    }

    //---------------------------------------------------------------------
    /**
       Gets the value in the field as a BigDecimal.
       @return BigDecimal the value in the field as a BigDecimal
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
            if (getDocument() instanceof NumericDocument)
            {
                //non zero numeric number (without any decimal)
                value = new BigDecimal(text);
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
        if (getDocument() instanceof DecimalDocument)
        {
            DecimalDocument doc = (DecimalDocument)getDocument();
            // retrieve decimal format from document.
            DecimalFormat df = doc.getFormat();
             setText(df.format(value));
        }
        if (getDocument() instanceof NumericDocument)
        {
            //non zero numeric number (without any decimal)
            setText(String.valueOf(value.intValue()));
        }
    }

    //---------------------------------------------------------------------
    /**
       Gets the Decimal Document
       @param maxLength maximum length
       @param zeroAllowed zero allowed or not
       @param decimalLength decimal length
       @return DecimalDocument decimal document
    */
    //---------------------------------------------------------------------
    public DecimalDocument getDecimalDocument(int maxLength,
                                              boolean zeroAllowed,
                                              int decimalLength)
    {
        DecimalDocument decimalDoc = new DecimalDocument(Integer.MAX_VALUE);
        decimalDoc.setMaxLength(maxLength);
        decimalDoc.setZeroAllowed(zeroAllowed);
        decimalDoc.setDecimalLength(decimalLength);
        return decimalDoc;
    }

    //---------------------------------------------------------------------
    /**
       Gets the numeric document
       @param maxLength maximum length
       @param zeroAllowed zero allowed or not
       @return NumericDocument numeric document
    */
    //---------------------------------------------------------------------
    public NumericDocument getNumericDocument(int maxLength,
                                              boolean zeroAllowed)
    {
        NumericDocument numericDoc = new NumericDocument(Integer.MAX_VALUE);
        numericDoc.setMaxLength(maxLength);
        numericDoc.setZeroAllowed(zeroAllowed);
        return numericDoc;
    }
}
