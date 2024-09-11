/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/AlphaReasonCodeBean.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:55 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *
 * ===========================================================================
 * $Log:
 *    4    I18N_P2    1.2.1.0     1/8/2008 2:56:48 PM    Sandy Gu        Set
 *         max length of constraied text field.
 *    3    360Commerce 1.2         3/31/2005 4:27:12 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:19:35 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:09:27 PM  Robert Pearse   
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
 *    Rev 1.0   Aug 29 2003 16:09:34   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.0   Apr 29 2002 14:47:40   msg
 * Initial revision.
 * 
 *    Rev 1.0   Mar 18 2002 11:52:14   msg
 * Initial revision.
 * 
 *    Rev 1.2   Mar 09 2002 10:46:14   mpm
 * More text externalization.
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.beans;

// java import
import javax.swing.JTextField;

//---------------------------------------------------------------------
/**
    This bean enables the editing of a reason code.
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//---------------------------------------------------------------------

public class AlphaReasonCodeBean extends ReasonCodeBean
{

    //---------------------------------------------------------------------
    /**
        Constructs bean.
    **/
    //---------------------------------------------------------------------
    public AlphaReasonCodeBean()
    {
        super();
    }

    //---------------------------------------------------------------------
    /**
        Initializes the class.
        @return the initialized database ID field
    **/
    //---------------------------------------------------------------------
    protected JTextField initializeDatabaseIDField()
    {
        ConstrainedTextField field =
            uiFactory.createConstrainedField("DatabaseIdTextField","0","8",false);
        field.setEmptyAllowed(false);
        field.setLabel(labels[DATABASE]);
        return field;
    }
}                                       // end class ReasonCodeBean
