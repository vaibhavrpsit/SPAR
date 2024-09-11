/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/ConstrainedPasswordField.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:43 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *   3    360Commerce 1.2         3/31/2005 4:27:30 PM   Robert Pearse   
 *   2    360Commerce 1.1         3/10/2005 10:20:23 AM  Robert Pearse   
 *   1    360Commerce 1.0         2/11/2005 12:10:11 PM  Robert Pearse   
 *
 *  Revision 1.4  2004/03/16 17:15:22  build
 *  Forcing head revision
 *
 *  Revision 1.3  2004/03/16 17:15:16  build
 *  Forcing head revision
 *
 *  Revision 1.2  2004/02/11 20:56:26  rhafernik
 *  @scr 0 Log4J conversion and code cleanup
 *
 *  Revision 1.1.1.1  2004/02/11 01:04:21  cschellenger
 *  updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 16:09:44   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.0   Sep 10 2002 14:38:32   baa
 * Initial revision.
 * Resolution for POS SCR-1810: Adding pasword validating fields
 * 
 *    Rev 1.0   Sep 10 2002 11:50:00   baa
 * Initial revision.
 * Resolution for kbpos SCR-2270: When changing password at POS, the password is visible to all
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.beans;

import javax.swing.text.Document;

import oracle.retail.stores.foundation.utility.Util;

//-------------------------------------------------------------------------
/**
   This field allows input to be valid if it meets max and min lenght
   requirements.
   @version $Revision: /rgbustores_13.4x_generic_branch/1 $;
*/
//-------------------------------------------------------------------------
public class ConstrainedPasswordField extends ValidatingPasswordField
{
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    //---------------------------------------------------------------------
    /**
       Constructor.
    */
    //---------------------------------------------------------------------
    public ConstrainedPasswordField()
    {
        this("");
    }

    //---------------------------------------------------------------------
    /**
       Constructor.
       @param value the default text for the field
    */
    //---------------------------------------------------------------------
    public ConstrainedPasswordField(String name)
    {
        this(name, 0, Integer.MAX_VALUE);
    }

    //---------------------------------------------------------------------
    /**
       Constructor.
       @param value the default text for the field
       @param minLength the minimum length for a valid field
       @param maxLength the maximum length for a valid field
    */
    //---------------------------------------------------------------------
    public ConstrainedPasswordField(String name, int minLength, int maxLength)
    {
        super();
        setName(name);
        setMinLength(minLength);
       ((ConstrainedTextDocument)getDocument()).setMaxLength(maxLength);
    }

    //---------------------------------------------------------------------
    /**
       Gets the default model for the Constrained field
       @return the model for length constrained fields
    */
    //---------------------------------------------------------------------
    protected Document createDefaultModel()
    {
        return new ConstrainedTextDocument(Integer.MAX_VALUE);
    }

    //---------------------------------------------------------------------
    /**
       Returns the maximum length of a valid field.
       @return the maximum length of a valid field
    */
    //---------------------------------------------------------------------
    public int getMaxLength()
    {
        return ((ConstrainedTextDocument)getDocument()).getMaxLength();
    }

    //---------------------------------------------------------------------
    /**
       Determines whether the current field information is valid and
       returns the result.
       @return true if the current field entry is valid, false otherwise
    */
    //---------------------------------------------------------------------
    public boolean isInputValid()
    {
        boolean rv = true;
        int len = getPassword().length;
        ConstrainedTextDocument doc = (ConstrainedTextDocument)getDocument();
        if (!super.isInputValid() ||
            len < getMinLength() ||
            len > doc.getMaxLength())
        {
            rv = false;
            // clear entered value if not valid
            setText("");
        }
        return rv;
    }

    //---------------------------------------------------------------------
    /**
       Sets the maximum length of a valid field.
       @param maxLength the maximum length for a valid field
    */
    //---------------------------------------------------------------------
    public void setMaxLength(int value)
    {
        ((ConstrainedTextDocument)getDocument()).setMaxLength(value);
       
    }

    //---------------------------------------------------------------------
    /**
       Returns default display string. <P>
       @return String representation of object
    */
    //---------------------------------------------------------------------
    public String toString()
    {
        String strResult = new String("Class: ConstrainedPasswordField (Revision " +
                                      getRevisionNumber() + ") @" +
                                      hashCode());
        return(strResult);
    }

    //---------------------------------------------------------------------
    /**
       Retrieves the PVCS revision number. <P>
       @return String representation of revision number
    */
    //---------------------------------------------------------------------
    public String getRevisionNumber()
    {
        return(Util.parseRevisionNumber(revisionNumber));
    }

}
