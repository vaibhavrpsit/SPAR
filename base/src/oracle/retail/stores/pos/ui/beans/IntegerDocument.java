/* ===========================================================================
* Copyright (c) 2004, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/IntegerDocument.java /main/18 2011/12/05 12:16:24 cgreene Exp $
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
 *
 * ===========================================================================
 * $Log:
 *    5    360Commerce 1.4         4/30/2008 12:14:36 PM  Kun Lu          Fix
 *         CR 31129. Hard code integer format and allow user to input integer
 *         in a free style
 *    4    360Commerce 1.3         4/25/2008 9:20:08 PM   Kun Lu          Fix
 *         CR 31129
 *    3    360Commerce 1.2         3/31/2005 4:28:23 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:22:08 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:11:26 PM  Robert Pearse   
 *
 *   Revision 1.2  2004/09/23 00:07:11  kmcbride
 *   @scr 7211: Adding static serialVersionUIDs to all POS Serializable objects, minus the JComponents
 *
 *   Revision 1.1  2004/04/16 18:56:33  tfritz
 *   @scr 4251 - Integer parameters now can except negative and positive integers.
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.beans;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;

import oracle.retail.stores.domain.utility.LocaleConstantsIfc;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.common.utility.LocaleMap;
//--------------------------------------------------------------------------
/**
   This document allows input to be valid if it meets max and min length
   requirements and is numeric (negative or positive number).

    $Revision: /main/18 $
**/
//--------------------------------------------------------------------------
public class IntegerDocument extends DecimalDocument
{
    // This id is used to tell
    // the compiler not to generate a
    // new serialVersionUID.
    //
    static final long serialVersionUID = -2586815350753099341L;

    /** revision number supplied by Team Connection */
    public static final String revisionNumber = "$Revision: /main/18 $";

    //---------------------------------------------------------------------
    /**
     Constructor.
     */
    //---------------------------------------------------------------------
    public IntegerDocument()
    {
        this(Integer.MAX_VALUE);
    }
    //---------------------------------------------------------------------
    /**
     Constructor.
     @param maxLength the maximum length
     */
    //---------------------------------------------------------------------
    public IntegerDocument(int maxLength)
    {
        super(maxLength, 0);
    }

    //---------------------------------------------------------------------
    /**
       Sets up the integer format.
    */
    //---------------------------------------------------------------------
   protected void setupFormat()
   {
       locale = LocaleMap.getLocale(LocaleConstantsIfc.USER_INTERFACE);
       format = DomainGateway.getFormat(locale, LocaleConstantsIfc.DEFAULT_INTEGER_FORMAT);

       symbols = format.getDecimalFormatSymbols();
       //default maxLength
       maxLength = format.getMaximumIntegerDigits();

        // Obtain fraction piece from the getCurrency.getfraction
       decLength = format.getMaximumFractionDigits();
       radix = Character.toString(getDefaultRadix());
   }


    //---------------------------------------------------------------------
    /**
     Determines if the text can be inserted.
     @param offset the offset at which the text should be inserted
     @param text the text to be inserted
     @param attributes the set of attributes for the text
     @exception BadLocationException if the offset is invalid
     */
    //---------------------------------------------------------------------
    public void insertString(int offset, String text, AttributeSet attributes)
    throws BadLocationException
    {
        if (text != null)
        {
            boolean numeric = true;
            int len = text.length();

            // Make sure there are not decimals entered, we are just interested
            // in positive or negative integer values
            for (int i=0; (i < len) && numeric ; ++i)
            {
                if (text.charAt(i) == radix.charAt(0))
                {
                    numeric = false;
                }
            }
            if (numeric)
            {
                super.insertString(offset, text, attributes);
            }
        }
    }
    //---------------------------------------------------------------------

    /**
     Determine if the dangling negative sign is allowed
     */
    @Override
    //---------------------------------------------------------------------
    protected boolean allowDanglingNegativeSign()
    {
        return true;
    }

    //---------------------------------------------------------------------
    /**
     Determine if user is allowed to delete negative sign
     */
    //---------------------------------------------------------------------
    @Override
    protected boolean allowDeleteNegativeSign()
    {
        return true;
    }


}
