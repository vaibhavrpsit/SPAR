/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/PriceCodeDocument.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:46 mszekely Exp $
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
 *    3    360Commerce 1.2         3/31/2005 4:29:28 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:24:20 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:13:22 PM  Robert Pearse   
 *
 *   Revision 1.4  2004/09/23 00:07:11  kmcbride
 *   @scr 7211: Adding static serialVersionUIDs to all POS Serializable objects, minus the JComponents
 *
 *   Revision 1.3  2004/03/16 17:15:18  build
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 20:56:27  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:22  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 16:11:40   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.0   Apr 29 2002 14:53:40   msg
 * Initial revision.
 * 
 *    Rev 1.0   Mar 18 2002 11:56:56   msg
 * Initial revision.
 * 
 *    Rev 1.1   Jan 19 2002 10:31:24   mpm
 * Initial implementation of pluggable-look-and-feel user interface.
 * Resolution for POS SCR-798: Implement pluggable-look-and-feel user interface
 * 
 *    Rev 1.0   Dec 20 2001 18:35:18   blj
 * Initial revision.
 * 
 *    Rev 1.2   Dec 10 2001 17:23:40   blj
 * updated per codereview findings.
 * Resolution for POS SCR-237: Gift Receipt Feature
 * 
 *    Rev 1.1   Dec 03 2001 16:47:16   blj
 * Updated for code review.
 * Resolution for POS SCR-237: Gift Receipt Feature
 * 
 *    Rev 1.0   Oct 29 2001 11:43:04   blj
 * Initial revision.
 * 
 *    Rev 1.0   Sep 21 2001 11:36:00   msg
 * Initial revision.
 * 
 *    Rev 1.1   Sep 17 2001 13:17:04   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.beans;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;

//-------------------------------------------------------------------------
/**
   This document allows input to be valid if it meets max and min length
   requirements, is numeric and is between the letters A and J.  This document
   also changes valid text to all caps.

   @version $Revision: /rgbustores_13.4x_generic_branch/1 $
*/
//-------------------------------------------------------------------------
class PriceCodeDocument extends ConstrainedTextDocument
{
    // This id is used to tell
    // the compiler not to generate a
    // new serialVersionUID.
    //
    static final long serialVersionUID = 2492656990896124246L;

    /** revision number supplied by PVCS */
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    //---------------------------------------------------------------------
    /**
       Constructor.
    */
    //---------------------------------------------------------------------
    public PriceCodeDocument()
    {
        this(Integer.MAX_VALUE);
    }

    //---------------------------------------------------------------------
    /**
       Constructor.
       @param maxLength the maximum length
    */
    //---------------------------------------------------------------------
    public PriceCodeDocument(int maxLength)
    {
        super(maxLength);
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
            boolean valid = true;
            int len = text.length();
            for (int i=0; (i < len) && valid ; ++i)
            {
                int numValue = Character.getNumericValue(text.charAt(i));
                // Input has to be a digit and who's numeric value is between 11 and 22 excluding 14, 18.
                // This ensures that only letters B through M, excluding all vowels are entered.
                if ((Character.isDigit(text.charAt(i))) 
                || (numValue < 11 || numValue > 22) 
                || numValue == 14 || numValue == 18)
                {
                    valid = false;
                }
                
            }
            if (valid)
            {
                text = text.toUpperCase();
                super.insertString(offset, text, attributes);
            }
        }
    }
}
