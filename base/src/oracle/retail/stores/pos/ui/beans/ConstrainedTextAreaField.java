/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/ConstrainedTextAreaField.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:44 mszekely Exp $
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
 *    4    I18N_P2    1.2.1.0     12/26/2007 9:54:39 AM  Maisa De Camargo CR
 *         29822 - I18N - Fixed Collapsing of Input Fields when labels are
 *         expanded.
 *    3    360Commerce 1.2         3/31/2005 4:27:30 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:20:23 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:10:11 PM  Robert Pearse   
 *
 *   Revision 1.3  2004/03/16 17:15:22  build
 *   Forcing head revision
 *
 *   Revision 1.2  2004/03/16 17:15:16  build
 *   Forcing head revision
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:21  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 16:09:46   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.1   Aug 07 2002 19:34:08   baa
 * remove hard coded date formats
 * Resolution for POS SCR-1740: Code base Conversions
 * 
 *    Rev 1.0   Apr 29 2002 14:55:46   msg
 * Initial revision.
 * 
 *    Rev 1.0   Mar 18 2002 11:54:32   msg
 * Initial revision.
 * 
 *    Rev 1.1   Jan 19 2002 10:29:24   mpm
 * Initial implementation of pluggable-look-and-feel user interface.
 * Resolution for POS SCR-798: Implement pluggable-look-and-feel user interface
 * 
 *    Rev 1.0   19 Dec 2001 17:44:42   baa
 * Initial revision.
 * Resolution for POS SCR-287: Send Transaction
 *
 *    Rev 1.0   Sep 21 2001 11:38:06   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 13:18:06   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.beans;

import java.awt.Dimension;

import javax.swing.text.Document;

//-------------------------------------------------------------------------
/**
   This field allows input to be valid if it meets max and min lenght
   requirements.
   @version $Revision: /rgbustores_13.4x_generic_branch/1 $
*/
//-------------------------------------------------------------------------
public class ConstrainedTextAreaField extends ValidatingTextAreaField
{
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";
    //---------------------------------------------------------------------
    /**
       Constructor.
    */
    //---------------------------------------------------------------------
    public ConstrainedTextAreaField()
    {
        this("");
    }

    //---------------------------------------------------------------------
    /**
       Constructor.
       @param value the default text for the field
    */
    //---------------------------------------------------------------------
    public ConstrainedTextAreaField(String value)
    {
        this(value, 0, Integer.MAX_VALUE);
    }

    //---------------------------------------------------------------------
    /**
       Constructor.
       @param value the default text for the field
       @param minLength the minimum length for a valid field
       @param maxLength the maximum length for a valid field
    */
    //---------------------------------------------------------------------
    public ConstrainedTextAreaField(String value, int minLength, int maxLength)
    {
        super(value);
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
        int len = getText().length();
        ConstrainedTextDocument doc = (ConstrainedTextDocument)getDocument();
        if (!super.isInputValid() ||
            len < getMinLength() ||
            len > doc.getMaxLength())
        {
            rv = false;
        }
        return rv;
    }

    //---------------------------------------------------------------------
    /**
       Sets the maximum length of a valid field.
       @param maxLength the maximum length for a valid field
    */
    //---------------------------------------------------------------------
    public void setMaxLength(int maxLength)
    {
                ((ConstrainedTextDocument)getDocument()).setMaxLength(maxLength);
    }

    //---------------------------------------------------------------------
    /**
       Returns default display string. <P>
       @return String representation of object
    */
    //---------------------------------------------------------------------
    public String toString()
    {
        String strResult = new String("Class: ConstrainedTextField (Revision " +
                                      getRevisionNumber() + ") @" +
                                      hashCode());
        return(strResult);
    }

    //---------------------------------------------------------------------
    /**
       Retrieves the Team Connection revision number. <P>
       @return String representation of revision number
    */
    //---------------------------------------------------------------------
    public String getRevisionNumber()
    {
        return(revisionNumber);
    }

    /**
     * Sets the minimum Size (minimum width) of the field
     * @param minimumSize
     */
    public void setMinimumSize (int minimumSize)
    {
        Dimension minimumFieldSizeDimension = getPreferredSize();
        minimumFieldSizeDimension.width = minimumSize * getColumnWidth();
        setMinimumSize(minimumFieldSizeDimension);
    }
    
}
