/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/SelectParameterBeanModel.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:59 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *   3    360Commerce 1.2         3/31/2005 4:29:54 PM   Robert Pearse   
 *   2    360Commerce 1.1         3/10/2005 10:25:09 AM  Robert Pearse   
 *   1    360Commerce 1.0         2/11/2005 12:14:08 PM  Robert Pearse   
 *
 *  Revision 1.4  2004/04/08 20:33:02  cdb
 *  @scr 4206 Cleaned up class headers for logs and revisions.
 *
 *  
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.beans;

// java imports

//----------------------------------------------------------------------------
/**
**/
//----------------------------------------------------------------------------
public class SelectParameterBeanModel extends SelectionListBeanModel
{
    /** revision number **/
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    protected String group = "*no group*";

    //-------------------------------------------------------------------------
    /**
        Returns the group. <p>
        @return the group
    **/
    //-------------------------------------------------------------------------
    public String getGroup()
    {
        return group;
    }

    //-------------------------------------------------------------------------
    /**
        Sets the Group field. <p>
        @param group    the value to be set for group
    **/
    //-------------------------------------------------------------------------
    public void setGroup(String group)
    {
        this.group = group;
    }
}
