/* ===========================================================================
* Copyright (c) 1998, 2013, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/AlphaNumericDocument.java /main/15 2013/04/04 14:36:38 abhineek Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    abhineek  04/03/13 - fix for user is not prompted with any error message
 *                         when a special character is entered in the password
 *                         field
 *    cgreene   03/18/11 - XbranchMerge cgreene_124_receipt_quick_wins from
 *                         main
 *    cgreene   03/16/11 - implement You Saved feature on reciept and
 *                         AllowMultipleQuantity parameter
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *    sgu       02/25/09 - fix max lenght for all item quantity fields
 *
 * ===========================================================================
 * $Log:
 *   5    360Commerce 1.4         10/10/2007 1:02:00 PM  Anda D. Cadar
 *        Changes to not allow double byte chars
 *   4    360Commerce 1.3         10/8/2007 11:36:46 AM  Anda D. Cadar   UI
 *        changes to not allow double bytes chars in some cases
 *   3    360Commerce 1.2         3/31/2005 4:27:12 PM   Robert Pearse
 *   2    360Commerce 1.1         3/10/2005 10:19:35 AM  Robert Pearse
 *   1    360Commerce 1.0         2/11/2005 12:09:26 PM  Robert Pearse
 *
 *  Revision 1.8  2004/09/23 00:07:11  kmcbride
 *  @scr 7211: Adding static serialVersionUIDs to all POS Serializable objects, minus the JComponents
 *
 *  Revision 1.7  2004/04/09 13:59:07  cdb
 *  @scr 4206 Cleaned up class headers for logs and revisions.
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.beans;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;

/**
 * This document allows input to be valid if it meets max and min length
 * requirements and is alpha numeric. The alpha characters will be converted to
 * uppercase.
 *
 * @version $Revision: /main/15 $
 */
class AlphaNumericDocument extends ConstrainedTextDocument
{
    // This id is used to tell
    // the compiler not to generate a
    // new serialVersionUID.
    //
    static final long serialVersionUID = 3328638075695939472L;

    /** revision number supplied by Team Connection */
    public static final String revisionNumber = "$KW=@(#); $Ver=pos_4.5.0:2; $EKW;";

    /**
     * attribute for allowing or not spaces
     */
    protected boolean isSpaceAllowed = false;
    
    /**
     *  attribute for allowing or not special Character
     */
    protected boolean isSpecialCharAllowed = false;

    /**
     * Constructor.
     */
    public AlphaNumericDocument()
    {
        this(Integer.MAX_VALUE);
    }

    /**
     * Constructor.
     *
     * @param maxLength the maximum length
     */
    public AlphaNumericDocument(int maxLength)
    {
        super(maxLength);
    }

    /**
     * Determines if the text can be inserted.
     *
     * @param offset the offset at which the text should be inserted
     * @param text the text to be inserted
     * @param attributes the set of attributes for the text
     * @exception BadLocationException if the offset is invalid
     */
    @Override
    public void insertString(int offset, String text, AttributeSet attributes) throws BadLocationException
    {
        if (text != null)
        {
            char[] buf = text.toCharArray();
            for (int i = 0; i < buf.length; ++i)
            {
                if (!isCharAllowable(buf[i]))
                {
                    return; // do not call super
                }
            }

            insertStringWithoutCharValidation(offset, new String(buf), attributes);
        }
    }

    /**
     * Returns whether the document allows the specified char to exist in the model.
     *
     * @param ch
     * @return
     * @see Character#isLetterOrDigit(char)
     * @see Character#isWhitespace(char)
     */
    protected boolean isCharAllowable(char ch)
    {
        if (Character.isWhitespace(ch))
        {
            return isSpaceAllowed();
        }
        if(isSpecialCharAllowed() && isSpecialCharacter(ch))
        {
            return true;
        }
        return Character.isLetterOrDigit(ch);
    }
    
    /**
     * @param ch
     * @return returns true if its a special character otherwise false
     */
    private boolean isSpecialCharacter(char ch)
    {
        boolean specialCharacter = false;

        if (!(ch >= 'A' && ch <= 'Z') && !(ch >= 'a' && ch <= 'z') && !(ch >= '0' && ch <= '9'))
        {
            specialCharacter = true;
        }

        return specialCharacter;
    }

    /**
     * @return Returns the isSpaceAllowed.
     */
    public boolean isSpaceAllowed()
    {
        return isSpaceAllowed;
    }

    /**
     * @param isSpaceAllowed The isSpaceAllowed to set.
     */
    public void setSpaceAllowed(boolean isSpaceAllowed)
    {
        this.isSpaceAllowed = isSpaceAllowed;
    }
    
    /**
     * @return Returns the isSpecialCharAllowed.
     */
    public boolean isSpecialCharAllowed()
    {
        return isSpecialCharAllowed;
    }
    
    /**
     * @param isSpecialCharAllowed The isSpecialCharAllowed to set.
     */
    public void setSpecialCharAllowed(boolean isSpecialCharAllowed)
    {
        this.isSpecialCharAllowed = isSpecialCharAllowed;
    }    

}
