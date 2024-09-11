/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/PriceCodeTextField.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:59 mszekely Exp $
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
 *   Revision 1.2  2004/03/16 17:15:18  build
 *   Forcing head revision
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:22  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 16:11:40   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.0   Apr 29 2002 14:53:42   msg
 * Initial revision.
 * 
 *    Rev 1.0   Mar 18 2002 11:56:58   msg
 * Initial revision.
 * 
 *    Rev 1.1   Jan 19 2002 10:31:26   mpm
 * Initial implementation of pluggable-look-and-feel user interface.
 * Resolution for POS SCR-798: Implement pluggable-look-and-feel user interface
 * 
 *    Rev 1.0   Dec 20 2001 18:35:18   blj
 * Initial revision.
 * 
 *    Rev 1.0   Oct 29 2001 11:43:04   blj
 * Initial revision.
 * 
 *    Rev 1.0   Sep 21 2001 11:36:22   msg
 * Initial revision.
 * 
 *    Rev 1.1   Sep 17 2001 13:17:04   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.beans;

import javax.swing.text.Document;

//-------------------------------------------------------------------------
/**
   This field allows input to be valid if it meets max and min lenght
   requirements.
   @version $Revision: /rgbustores_13.4x_generic_branch/1 $
*/
//-------------------------------------------------------------------------
public class PriceCodeTextField extends ConstrainedTextField
{
    /** revision number supplied by Team Connection */
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    //---------------------------------------------------------------------
    /**
       Constructor.
    */
    //---------------------------------------------------------------------
    public PriceCodeTextField()
    {
        this("");
    }

    //---------------------------------------------------------------------
    /**
       Constructor.
       @param value the default text for the field
    */
    //---------------------------------------------------------------------
    public PriceCodeTextField(String value)
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
    public PriceCodeTextField(String value, int minLength, int maxLength)
    {
        super(value, minLength, maxLength);
    }

    //---------------------------------------------------------------------
    /**
       Gets the default model for the Constrained field
       @return the model for length constrained fields
    */
    //---------------------------------------------------------------------
    protected Document createDefaultModel()
    {
        return new PriceCodeDocument(Integer.MAX_VALUE);
    }
}
