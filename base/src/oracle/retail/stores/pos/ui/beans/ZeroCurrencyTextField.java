/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/ZeroCurrencyTextField.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:46 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *5    360Commerce 1.4         4/25/2007 8:51:25 AM   Anda D. Cadar   I18N
 *     merge
 *4    360Commerce 1.3         1/22/2006 11:45:29 AM  Ron W. Haight   removed
 *     references to com.ibm.math.BigDecimal
 *3    360Commerce 1.2         3/31/2005 4:30:59 PM   Robert Pearse   
 *2    360Commerce 1.1         3/10/2005 10:27:05 AM  Robert Pearse   
 *1    360Commerce 1.0         2/11/2005 12:16:09 PM  Robert Pearse   
 *
 Revision 1.3  2004/03/16 17:15:19  build
 Forcing head revision
 *
 Revision 1.2  2004/02/11 20:56:26  rhafernik
 @scr 0 Log4J conversion and code cleanup
 *
 Revision 1.1.1.1  2004/02/11 01:04:23  cschellenger
 updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 16:13:06   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.2   Dec 03 2002 16:45:32   vxs
 * Deprecated the classes. Now use CurrencyTextField.setZeroAllowed()
 * 
 *    Rev 1.1   Aug 07 2002 19:34:30   baa
 * remove hard coded date formats
 * Resolution for POS SCR-1740: Code base Conversions
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.beans;

import java.text.DecimalFormat;
import java.text.ParseException;

import javax.swing.text.Document;

import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.domain.DomainGateway;
import java.math.BigDecimal;
//-------------------------------------------------------------------------
/**
   This field allows decimal numbers only. Also accepts zero as input.
   @version $KW=@(#); $Ver=pos_4.5.0:2; $EKW;
   @deprecated as of release 5.5 Replaced by CurrencyTextField.setZeroAllowed(boolean)
*/
//-------------------------------------------------------------------------
public class ZeroCurrencyTextField extends ValidatingTextField
{
    /** 
        revision number supplied by Team Connection 
    **/
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";
    /** 
        the length of the text field without radix point 
    **/
    protected int maxLength;

    //---------------------------------------------------------------------
    /**
       Constructor.
    */
    //---------------------------------------------------------------------
    public ZeroCurrencyTextField()
    {
        this("");
    }

    //---------------------------------------------------------------------
    /**
       Constructor.
       @param value the default text for the field
    */
    //---------------------------------------------------------------------
    public ZeroCurrencyTextField(String value)
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
    public ZeroCurrencyTextField(String value, int maxLength)
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
    public ZeroCurrencyTextField(String value, int maxLength, int decLength)
    {
        super(value);
        setMaxLength(maxLength);
        setDecimalLength(decLength);
    }

    //---------------------------------------------------------------------
    /**
    */
    //---------------------------------------------------------------------
    public void setDecimalLength(int decLength)
    {
        ((ZeroCurrencyDocument)getDocument()).setDecimalLength(decLength);
    }
    //---------------------------------------------------------------------
    /**
    */
    //---------------------------------------------------------------------
    public void setMaxLength(int maxLength)
    {
        if (maxLength >= Integer.MAX_VALUE)
        {
            maxLength -= 1;
        }
        this.maxLength = maxLength;
        ((ZeroCurrencyDocument)getDocument()).setMaxLength(maxLength);
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
        return new ZeroCurrencyDocument(Integer.MAX_VALUE);
    }
    //---------------------------------------------------------------------
    /**
       Gets the value in the field as a String.
       @return the value in the field as a String
    */
    //---------------------------------------------------------------------
    public BigDecimal getDecimalValue()
    {
        BigDecimal value;
        if (getDocument() instanceof ZeroCurrencyDocument)
        {
            ZeroCurrencyDocument doc = (ZeroCurrencyDocument)getDocument();
            
            StringBuffer pattern =
                new StringBuffer(doc.getPad() + doc.getRadix());
            for (int i=0; i < doc.getDecimalLength(); ++i)
            {
                pattern.append(doc.getPad());
            }
            String sub = pattern.toString();
            pattern.append(";");
            pattern.append(doc.getNegPrefix());
            pattern.append(sub);
            pattern.append(doc.getNegSuffix());
            DecimalFormat format = new DecimalFormat(pattern.toString());
            try
            {
                value = new BigDecimal(format.parse(getText()).doubleValue());
            }
            catch (ParseException excp)
            {
                value = new BigDecimal(Double.valueOf(getText()).doubleValue());
            }
        }
        else
        {
            value = new BigDecimal(Double.valueOf(getText()).doubleValue());
        }
        return value;
    }
    //---------------------------------------------------------------------
    /**
       Gets the value in the field as a BigDecimal.
       @return the value in the field as a BigDecimal
    */
    //---------------------------------------------------------------------
    public CurrencyIfc getCurrencyValue()
    {
        CurrencyIfc c = DomainGateway.getBaseCurrencyInstance();
        c.setStringValue(getDecimalValue().toString());
        return(c);
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
        if (getDocument() instanceof ZeroCurrencyDocument)
        {
            rv = ((ZeroCurrencyDocument)getDocument()).isNegativeAllowed();
        }
        else
        {
            rv = true;
        }
        return rv;
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
        setDecimalValue(new BigDecimal(dollar.getStringValue()));
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
        if (getDocument() instanceof ZeroCurrencyDocument)
        {
            ZeroCurrencyDocument doc = (ZeroCurrencyDocument)getDocument();
            BigDecimal newValue = new BigDecimal(Math.round(value.doubleValue() * Math.pow(10, doc.getDecimalLength())));
            setCaretPosition(doc.getLength());
            boolean edit = isEditable();
            setEditable(true);
            super.setText(newValue.toString());
            setEditable(edit);
        }
        else
        {
            boolean edit = isEditable();
            setEditable(true);
            super.setText(value.toString());
            setEditable(edit);
        }
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
        if (getDocument() instanceof ZeroCurrencyDocument)
        {
            ((ZeroCurrencyDocument)getDocument()).setNegativeAllowed(negativeAllowed);
        }
    }
}
