/* ===========================================================================
* Copyright (c) 2008, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/NumericDocument.java /main/14 2011/12/05 12:16:24 cgreene Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   12/05/11 - updated from deprecated packages and used more
 *                         bigdecimal constants
 *    cgreene   06/17/11 - added method isNumeric
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/26/10 - XbranchMerge cgreene_tech43 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    cgreene   04/02/10 - remove deprecated LocaleContantsIfc and currencies
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 * 5    360Commerce 1.4         4/4/2008 1:16:48 PM    Sameer Thajudin The
 *      zeroDigit has to be initialized. Not initializing it will allow entry
 *      of zero even when 'zeroAllowed' is false.
 *
 *      The zeroDigit is initialized in the constructor public
 *      NumericDocument(int).  
 * 4    360Commerce 1.3         11/13/2007 2:38:51 PM  Jack G. Swan    Modified
 *       to support retrieving a byte array from the UI for card numbers
 *      instead of a String object. 
 * 3    360Commerce 1.2         3/31/2005 4:29:10 PM   Robert Pearse   
 * 2    360Commerce 1.1         3/10/2005 10:23:44 AM  Robert Pearse   
 * 1    360Commerce 1.0         2/11/2005 12:12:48 PM  Robert Pearse   
 *
 * Revision 1.4  2004/09/23 00:07:11  kmcbride
 * @scr 7211: Adding static serialVersionUIDs to all POS Serializable objects, minus the JComponents
 *
 * Revision 1.3  2004/03/16 17:15:18  build
 * Forcing head revision
 *
 * Revision 1.2  2004/02/11 20:56:27  rhafernik
 * @scr 0 Log4J conversion and code cleanup
 *
 * Revision 1.1.1.1  2004/02/11 01:04:22  cschellenger
 * updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 16:11:18   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.4   Jul 23 2003 17:27:36   baa
 * numeric values getting inserted multiple times
 * Resolution for 3231: Unable to retrive a suspended gift card transaction by scanning or entering.
 * 
 *    Rev 1.3   Jul 22 2003 11:13:28   baa
 * remove system.out
 * 
 *    Rev 1.2   Jul 22 2003 10:38:34   baa
 * add zeroAllow property
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.beans;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.GapContent;

import oracle.retail.stores.domain.utility.LocaleConstantsIfc;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.common.utility.LocaleMap;

/**
 * This document allows input to be valid if it meets max and min length
 * requirements and is numeric.
 * 
 * @version $KW=@(#); $Ver=pos_4.5.0:2; $EKW;
 */
class NumericDocument extends ConstrainedTextDocument
{
    // This id is used to tell the compiler not to generate a new serialVersionUID.
    static final long serialVersionUID = -5976187536032664534L;

    /** revision number supplied by Team Connection */
    public static final String revisionNumber = "$Revision: /main/14 $";

    /** the flag to allow zero values */
    protected boolean zeroAllowed;

    protected String zeroDigit;

    /**
     * Constructor.
     */
    public NumericDocument()
    {
        this(Integer.MAX_VALUE);
    }

    /**
     * Constructor.
     * 
     * @param maxLength the maximum length
     */
    public NumericDocument(int maxLength)
    {
        super(maxLength, new GapContent());

        /*
         * The zeroDigit has to be initialized. Not initializing it will allow
         * entry of zero even when 'zeroAllowed' is false.
         */
        Locale locale = LocaleMap.getLocale(LocaleConstantsIfc.USER_INTERFACE);
        DecimalFormat format = DomainGateway.getDecimalFormat(locale);
        DecimalFormatSymbols symbols = format.getDecimalFormatSymbols();
        zeroDigit = new Character(symbols.getZeroDigit()).toString();
    }

    /**
     * Constructor.
     * 
     * @param maxLength the maximum length
     */
    public NumericDocument(int maxLength, Content content)
    {
        super(maxLength, content);
        Locale locale = LocaleMap.getLocale(LocaleConstantsIfc.USER_INTERFACE);
        DecimalFormat format = DomainGateway.getDecimalFormat(locale);
        DecimalFormatSymbols symbols = format.getDecimalFormatSymbols();
        zeroDigit = new Character(symbols.getZeroDigit()).toString();
    }

    /**
     * Determines if the text can be inserted. Overridden to call
     * 
     * @param offset the offset at which the text should be inserted
     * @param text the text to be inserted
     * @param attributes the set of attributes for the text
     * @exception BadLocationException if the offset is invalid
     */
    @Override
    public void insertString(int offset, String text, AttributeSet attributes) throws BadLocationException
    {
        if (text != null && isNumeric(text))
        {
            if (getLength() > 0)
            {
                super.insertString(offset, text, attributes);
            }
            else if (zeroAllowed || (!zeroAllowed && !text.equals(zeroDigit)))
            {
                super.insertString(offset, text, attributes);
            }
        }
    }

    /**
     * Sets the flag to allow or disallow a zero value.
     * 
     * @param zeroAllowed true if a zero value should be allowed, false
     *            otherwise
     */
    public void setZeroAllowed(boolean zeroAllowed)
    {
        this.zeroAllowed = zeroAllowed;
    }

    /**
     * Returns whether a zero value is allowed in this field.
     * 
     * @return true if zero value is allowed, false otherwise
     */
    public boolean isZeroAllowed()
    {
        return zeroAllowed;
    }

    /**
     * Determines if all the chars in the specified text are digits.
     * 
     * @param text
     * @return
     * @see Character#isDigit(char)
     */
    protected boolean isNumeric(String text)
    {
        boolean numeric = true;
        int len = text.length();

        for (int i = 0; i < len; i++)
        {
            if (!Character.isDigit(text.charAt(i)))
            {
                numeric = false;
                break;
            }
        }
        return numeric;
    }
}
