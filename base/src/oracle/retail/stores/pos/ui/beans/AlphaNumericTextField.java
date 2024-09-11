/* ===========================================================================
* Copyright (c) 1999, 2013, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/AlphaNumericTextField.java /main/16 2013/06/04 17:39:14 cgreene Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   06/04/13 - removed toString method since it is less useful than
 *                         the normal toString
 *    cgreene   05/28/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
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
 *   1    360Commerce 1.0         2/11/2005 12:09:27 PM  Robert Pearse
 *
 *  Revision 1.6  2004/04/09 13:59:07  cdb
 *  @scr 4206 Cleaned up class headers for logs and revisions.
 *
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.beans;

import javax.swing.text.Document;

import oracle.retail.stores.common.utility.Util;

/**
 * This field allows input to be valid if it meets max and min length
 * requirements and is alpha numeric. All input will be converted to uppercase.
 * 
 * @version $Revision: /main/16 $
 */
public class AlphaNumericTextField extends ConstrainedTextField
{
    private static final long serialVersionUID = 183759964598155534L;

    /**
     * revision number
     */
    public static final String revisionNumber = "$Revision: /main/16 $";

    /**
     * attribute for allowing or not spaces
     */
    protected boolean isSpaceAllowed = false;

    /**
     * Constructor.
     */
    public AlphaNumericTextField()
    {
        this("");
    }

    /**
     * Constructor.
     * 
     * @param value the default text for the field
     */
    public AlphaNumericTextField(String value)
    {
        this(value, 0, Integer.MAX_VALUE);
    }

    /**
     * Constructor.
     * 
     * @param value the default text for the field
     * @param minLength the minimum length for a valid field
     * @param maxLength the maximum length for a valid field
     */
    public AlphaNumericTextField(String value, int minLength, int maxLength)
    {
        super(value, minLength, maxLength);
    }

    /**
     * Constructor that indicates if double byte chars are allowed in the field
     * or not. Added in I18N Phase 2 to indicate that some of the field should
     * not allow double bytes
     * 
     * @param value the default text for the field
     * @param minLength the minimum length for a valid field
     * @param maxLength the maximum length for a valid field
     * @param doubleBytesCharsAllowed boolean
     */
    public AlphaNumericTextField(String value, int minLength, int maxLength, boolean doubleBytesCharsAllowed)
    {
        super(value, minLength, maxLength, doubleBytesCharsAllowed);

    }

    /**
     * Gets the default model for the Constrained field
     * 
     * @return the model for length constrained fields
     */
    protected Document createDefaultModel()
    {
        return new AlphaNumericDocument(Integer.MAX_VALUE);
    }

    /**
     * Gets isSpaceAllowed flag.
     * 
     * @return Returns the isSpaceAllowed.
     */
    public boolean isSpaceAllowed()
    {
        return isSpaceAllowed;
    }

    /**
     * Sets isSpaceAllowed flag.
     * 
     * @param value set the space allowed flag
     */
    public void setSpaceAllowed(boolean value)
    {
        ((AlphaNumericDocument)getDocument()).setSpaceAllowed(value);
    }

    /**
     * Retrieves the Team Connection revision number.
     * 
     * @return String representation of revision number
     */
    public String getRevisionNumber()
    {
        return (Util.parseRevisionNumber(revisionNumber));
    }
}